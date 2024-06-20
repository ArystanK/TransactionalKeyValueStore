package kz.arctan.transactional_key_value_store.presentation.intent_handler

import kz.arctan.transactional_key_value_store.domain.updateLast
import kz.arctan.transactional_key_value_store.presentation.ApplicationState

class SetValueIntentHandler {
    operator fun invoke(
        key: String,
        value: String,
        setState: ((ApplicationState) -> ApplicationState) -> Unit
    ) {
        setState { state ->
            state.copy(
                transactions = state.transactions.updateLast {
                    it.copy(state = it.state + (key to value))
                },
            )
        }
    }
}