package kz.arctan.transactional_key_value_store.presentation.intent_handler

import kotlinx.collections.immutable.toPersistentList
import kz.arctan.transactional_key_value_store.presentation.ApplicationState
import kz.arctan.transactional_key_value_store.presentation.Output
import kz.arctan.transactional_key_value_store.presentation.UiText
import transactionalkeyvaluestore.composeapp.generated.resources.Res
import transactionalkeyvaluestore.composeapp.generated.resources.no_transaction
import kotlin.random.Random

class RollbackIntentHandler {
    operator fun invoke(
        uiState: ApplicationState,
        setState: ((ApplicationState) -> ApplicationState) -> Unit
    ) {
        if (uiState.numberOfTransactions > 0) {
            setState { state ->
                state.copy(
                    transactions = state.transactions.pop().second,
                    numberOfTransactions = state.numberOfTransactions - 1
                )
            }
        } else {
            setState { state ->
                state.copy(
                    outputs = (state.outputs + Output.Result(
                        result = UiText.Res(Res.string.no_transaction),
                        key = Random.nextLong()
                    )).toPersistentList()
                )
            }
        }
    }
}