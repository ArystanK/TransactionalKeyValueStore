package kz.arctan.transactional_key_value_store.domain.use_case

import kz.arctan.transactional_key_value_store.domain.PersistentStack
import kz.arctan.transactional_key_value_store.domain.Transaction

class CountValueUseCase {
    operator fun invoke(transactions: PersistentStack<Transaction>, value: String): Int =
        transactions.peek()
            ?.state
            ?.count { it.value == value }
            ?: 0
}