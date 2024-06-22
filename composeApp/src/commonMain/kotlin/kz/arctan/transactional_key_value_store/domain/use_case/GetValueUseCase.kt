package kz.arctan.transactional_key_value_store.domain.use_case

import kz.arctan.transactional_key_value_store.domain.PersistentStack
import kz.arctan.transactional_key_value_store.domain.Transaction
import kz.arctan.transactional_key_value_store.domain.find

class GetValueUseCase {
    operator fun invoke(key: String, transactions: PersistentStack<Transaction>): String? =
        transactions
            .find { it.state.containsKey(key) }
            ?.state
            ?.get(key)
}
