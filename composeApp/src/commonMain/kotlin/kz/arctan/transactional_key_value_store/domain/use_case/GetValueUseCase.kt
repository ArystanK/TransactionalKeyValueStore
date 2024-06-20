package kz.arctan.transactional_key_value_store.domain.use_case

import kz.arctan.transactional_key_value_store.domain.PersistentStack
import kz.arctan.transactional_key_value_store.domain.Transaction

class GetValueUseCase {
    operator fun invoke(key: String, transactions: PersistentStack<Transaction>): String? =
        transactions.find(key)
}

fun PersistentStack<Transaction>.find(key: String): String? {
    var stack = this
    while (!stack.isEmpty()) {
        val (head, tail) = pop()
        head?.let { nonNullHead ->
            nonNullHead.state[key]?.let { return it }
        }
        stack = tail
    }
    return null
}



