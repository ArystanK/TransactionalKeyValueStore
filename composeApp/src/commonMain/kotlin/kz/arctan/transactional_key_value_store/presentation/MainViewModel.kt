package kz.arctan.transactional_key_value_store.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.arctan.transactional_key_value_store.domain.*
import transactionalkeyvaluestore.composeapp.generated.resources.Res
import transactionalkeyvaluestore.composeapp.generated.resources.key_not_set
import transactionalkeyvaluestore.composeapp.generated.resources.no_transaction

class MainViewModel(
    private val operationsRepository: OperationsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ApplicationState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val operations = operationsRepository.getAll()
            _uiState.update { state ->
                state.copy(
                    transactions = operations.toTransactions(),
                    outputs = operations.map { operation -> Output.Operation(operation) }
                )
            }
        }
    }

    fun reduce(intent: ApplicationIntent) {
        when (intent) {
            is ApplicationIntent.ExecuteOperationIntent -> {
                val operation =
                    OperationModel(intent.command, _uiState.value.commandKey, _uiState.value.commandValue) ?: return
                viewModelScope.launch {
                    operationsRepository.addOperation(operationModel = operation)
                }

                _uiState.update { state ->
                    state.copy(outputs = state.outputs + Output.Operation(operation))
                }

                when (operation) {
                    OperationModel.Begin -> _uiState.update { state ->
                        state.copy(
                            transactions = state.transactions.push(
                                Transaction(
                                    state = state.transactions.peek()?.state ?: emptyMap()
                                )
                            ),
                            numberOfTransactions = state.numberOfTransactions + 1
                        )
                    }

                    OperationModel.Commit -> if (_uiState.value.numberOfTransactions > 0) {
                        _uiState.update { state ->
                            val (last, previousTransactions) = state.transactions.pop()
                            state.copy(
                                transactions = previousTransactions
                                    .updateLast { transaction ->
                                        transaction.copy(state = last?.state ?: transaction.state)
                                    },
                                numberOfTransactions = state.numberOfTransactions - 1
                            )
                        }
                    } else {
                        _uiState.update { state ->
                            state.copy(outputs = state.outputs + Output.Result(UiText.Res(Res.string.no_transaction)))
                        }
                    }

                    is OperationModel.Count -> _uiState.update { state ->
                        state.copy(
                            outputs = state.outputs + Output.Result(
                                result = UiText.Str(
                                    state
                                        .transactions
                                        .peek()
                                        ?.state
                                        ?.count { it.value == operation.value }
                                        .toString()
                                )
                            )
                        )
                    }

                    is OperationModel.Delete -> _uiState.update { state ->
                        state.copy(
                            transactions = state.transactions.updateLast { transaction ->
                                transaction.copy(
                                    state = state
                                        .transactions
                                        .peek()
                                        ?.state
                                        ?.minus(operation.key)
                                        ?: emptyMap()
                                )
                            }
                        )
                    }

                    is OperationModel.Get -> _uiState.update { state ->
                        state.copy(
                            outputs = state.outputs + Output.Result(
                                result = state.transactions.peek()?.state?.get(operation.key)
                                    ?.let { UiText.Str(it) }
                                    ?: UiText.Res(Res.string.key_not_set)
                            )
                        )
                    }

                    OperationModel.Rollback -> if (_uiState.value.numberOfTransactions > 0) {
                        _uiState.update { state ->
                            state.copy(
                                transactions = state.transactions.pop().second,
                                numberOfTransactions = state.numberOfTransactions - 1
                            )
                        }
                    } else {
                        _uiState.update { state ->
                            state.copy(outputs = state.outputs + Output.Result(UiText.Res(Res.string.no_transaction)))
                        }
                    }

                    is OperationModel.Set -> _uiState.update { state ->
                        state.copy(
                            transactions = state.transactions.updateLast {
                                it.copy(state = it.state + (operation.key to operation.value))
                            },
                        )
                    }
                }
            }

            ApplicationIntent.DismissDropDownMenuIntent -> _uiState.update { state ->
                state.copy(
                    isExpandedDropDownMenu = false
                )
            }

            is ApplicationIntent.DropDownMenuItemClickIntent -> _uiState.update {
                it.copy(selectedCommand = UiText.Str(intent.operation), isExpandedDropDownMenu = false)
            }

            ApplicationIntent.ShowDropDownMenu -> _uiState.update { it.copy(isExpandedDropDownMenu = true) }
            is ApplicationIntent.CommandKeyChangeIntent -> _uiState.update { it.copy(commandKey = intent.key) }
            is ApplicationIntent.CommandValueChangeIntent -> _uiState.update { it.copy(commandValue = intent.value) }
        }
    }
}
