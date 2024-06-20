import kz.arctan.transactional_key_value_store.data.InMemoryOperationsRepository
import kz.arctan.transactional_key_value_store.domain.use_case.CommitTransactionUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.CountValueUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.DeleteValueUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.GetTransactionsUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.GetValueUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.ParseCommandUseCase
import kz.arctan.transactional_key_value_store.presentation.ApplicationIntent
import kz.arctan.transactional_key_value_store.presentation.MainViewModel
import kz.arctan.transactional_key_value_store.presentation.intent_handler.RollbackIntentHandler
import kz.arctan.transactional_key_value_store.presentation.intent_handler.SetValueIntentHandler
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionsTest {
    @Test
    fun testDelete() {
        val viewModel = MainViewModel(
            operationsRepository = InMemoryOperationsRepository(),
            countValueUseCase = CountValueUseCase(),
            commitTransactionUseCase = CommitTransactionUseCase(),
            deleteValueUseCase = DeleteValueUseCase(),
            getTransactionsUseCase = GetTransactionsUseCase(),
            getValueUseCase = GetValueUseCase(),
            parseCommandUseCase = ParseCommandUseCase(),
            rollbackIntentHandler = RollbackIntentHandler(),
            setValueIntentHandler = SetValueIntentHandler()
        )

        viewModel.reduce(ApplicationIntent.CommandKeyChangeIntent("321"))
        viewModel.reduce(ApplicationIntent.CommandValueChangeIntent("321"))
        viewModel.reduce(ApplicationIntent.ExecuteOperationIntent("SET"))
        assertEquals(viewModel.uiState.value.transactions.pop().first?.state?.get("321"), "321")
        viewModel.reduce(ApplicationIntent.ExecuteOperationIntent("DELETE"))

        assertEquals(viewModel.uiState.value.transactions.pop().first?.state?.get("321"), null)
    }

    @Test
    fun testTransactionalDelete() {
        val viewModel = MainViewModel(
            operationsRepository = InMemoryOperationsRepository(),
            countValueUseCase = CountValueUseCase(),
            commitTransactionUseCase = CommitTransactionUseCase(),
            deleteValueUseCase = DeleteValueUseCase(),
            getTransactionsUseCase = GetTransactionsUseCase(),
            getValueUseCase = GetValueUseCase(),
            parseCommandUseCase = ParseCommandUseCase(),
            rollbackIntentHandler = RollbackIntentHandler(),
            setValueIntentHandler = SetValueIntentHandler()
        )
        viewModel.reduce(ApplicationIntent.CommandKeyChangeIntent("321"))
        viewModel.reduce(ApplicationIntent.CommandValueChangeIntent("321"))
        viewModel.reduce(ApplicationIntent.ExecuteOperationIntent("SET"))
        assertEquals(viewModel.uiState.value.transactions.pop().first?.state?.get("321"), "321")
        viewModel.reduce(ApplicationIntent.ExecuteOperationIntent("BEGIN"))
        viewModel.reduce(ApplicationIntent.ExecuteOperationIntent("DELETE"))

        assertEquals(viewModel.uiState.value.transactions.pop().first?.state?.get("321"), null)
    }
}