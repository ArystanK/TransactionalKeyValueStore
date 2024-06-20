package kz.arctan.transactional_key_value_store.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.arctan.transactional_key_value_store.domain.OperationModel
import kz.arctan.transactional_key_value_store.domain.OperationsRepository
import kz.arctan.transactional_key_value_store.domain.Transaction
import kz.arctan.transactional_key_value_store.domain.use_case.CommitTransactionUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.CountValueUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.DeleteValueUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.GetTransactionsUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.GetValueUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.ParseCommandUseCase
import kz.arctan.transactional_key_value_store.presentation.intent_handler.RollbackIntentHandler
import kz.arctan.transactional_key_value_store.presentation.intent_handler.SetValueIntentHandler
import transactionalkeyvaluestore.composeapp.generated.resources.Res
import transactionalkeyvaluestore.composeapp.generated.resources.key_not_set
import transactionalkeyvaluestore.composeapp.generated.resources.no_transaction
import kotlin.random.Random

class MainViewModel(
    private val commitTransactionUseCase: CommitTransactionUseCase,
    private val operationsRepository: OperationsRepository,
    private val parseCommandUseCase: ParseCommandUseCase,
    private val countValueUseCase: CountValueUseCase,
    private val getValueUseCase: GetValueUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteValueUseCase: DeleteValueUseCase,

    private val rollbackIntentHandler: RollbackIntentHandler,
    private val setValueIntentHandler: SetValueIntentHandler
) : ViewModel() {
    private val _uiState = MutableStateFlow(ApplicationState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { operationsRepository.getAll() }
                .onSuccess { operations ->
                    val transactions = getTransactionsUseCase(operations)

                    _uiState.update { state ->
                        state.copy(
                            transactions = transactions,
                            outputs = operations.map { operation ->
                                Output.Operation(
                                    operationModel = operation,
                                    key = Random.nextLong()
                                )
                            }.toPersistentList()
                        )
                    }
                }
        }
    }

    fun reduce(intent: ApplicationIntent) {
        viewModelScope.launch {
            when (intent) {
                is ApplicationIntent.ExecuteOperationIntent -> {
                    parseCommandUseCase(
                        intent.command,
                        uiState.value.commandKey,
                        uiState.value.commandValue
                    )
                        .onRight { operation ->
                            withContext(Dispatchers.IO) {
                                operationsRepository.addOperation(operationModel = operation)
                            }

                            _uiState.update { state ->
                                state.copy(
                                    outputs = (state.outputs + Output.Operation(
                                        operationModel = operation,
                                        key = Random.nextLong()
                                    )).toPersistentList()
                                )
                            }

                            when (operation) {
                                OperationModel.Begin -> {
                                    _uiState.update { state ->
                                        state.copy(
                                            transactions = state.transactions.push(Transaction()),
                                            numberOfTransactions = state.numberOfTransactions + 1
                                        )
                                    }
                                }

                                OperationModel.Commit -> {
                                    if (uiState.value.numberOfTransactions > 0) {
                                        val commitedTransactions = commitTransactionUseCase(
                                            _uiState.value.transactions
                                        )
                                        _uiState.update { state ->
                                            state.copy(
                                                transactions = commitedTransactions,
                                                numberOfTransactions = state.numberOfTransactions - 1
                                            )
                                        }
                                    } else {
                                        _uiState.update { state ->
                                            state.copy(
                                                outputs = (state.outputs + Output.Result(
                                                    result = UiText.Res(Res.string.no_transaction),
                                                    key = Random.nextLong()
                                                )).toPersistentList()
                                            )
                                        }
                                    }
                                }

                                is OperationModel.Count -> {
                                    val countResult = countValueUseCase(
                                        _uiState.value.transactions,
                                        operation.value
                                    ).toString()

                                    _uiState.update { state ->
                                        state.copy(
                                            outputs = (state.outputs + Output.Result(
                                                result = UiText.Str(string = countResult),
                                                key = Random.nextLong()
                                            )).toPersistentList()
                                        )
                                    }
                                }

                                is OperationModel.Delete -> {
                                    val deletedTransactions = deleteValueUseCase(
                                        transactions = uiState.value.transactions,
                                        key = operation.key
                                    )
                                    _uiState.update { state ->
                                        state.copy(transactions = deletedTransactions)
                                    }
                                }

                                is OperationModel.Get -> {
                                    val value =
                                        getValueUseCase(operation.key, uiState.value.transactions)

                                    _uiState.update { state ->
                                        state.copy(
                                            outputs = (state.outputs + Output.Result(
                                                result = value?.let { UiText.Str(it) }
                                                    ?: UiText.Res(Res.string.key_not_set),
                                                key = Random.nextLong()
                                            )).toPersistentList(),
                                        )
                                    }
                                }

                                OperationModel.Rollback -> {
                                    rollbackIntentHandler(
                                        uiState = uiState.value,
                                        setState = _uiState::update
                                    )
                                }

                                is OperationModel.Set -> {
                                    setValueIntentHandler(
                                        key = operation.key,
                                        value = operation.value,
                                        setState = _uiState::update
                                    )
                                }
                            }
                        }
                }

                ApplicationIntent.DismissDropDownMenuIntent -> _uiState.update { state ->
                    state.copy(isExpandedDropDownMenu = false)
                }

                is ApplicationIntent.DropDownMenuItemClickIntent -> _uiState.update {
                    it.copy(
                        selectedCommand = UiText.Str(intent.operation),
                        isExpandedDropDownMenu = false
                    )
                }

                ApplicationIntent.ShowDropDownMenu -> _uiState.update {
                    it.copy(isExpandedDropDownMenu = true)
                }

                is ApplicationIntent.CommandKeyChangeIntent -> _uiState.update {
                    it.copy(commandKey = intent.key)
                }

                is ApplicationIntent.CommandValueChangeIntent -> _uiState.update {
                    it.copy(commandValue = intent.value)
                }
            }
        }
    }
}
