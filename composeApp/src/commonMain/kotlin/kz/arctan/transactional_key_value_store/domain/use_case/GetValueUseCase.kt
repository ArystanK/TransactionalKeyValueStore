package kz.arctan.transactional_key_value_store.domain.use_case

import kz.arctan.transactional_key_value_store.domain.PersistentStack
import kz.arctan.transactional_key_value_store.domain.Transaction

class GetValueUseCase {
    operator fun invoke(key: String, transactions: PersistentStack<Transaction>): String? =
        transactions.find(key)
}

tailrec fun PersistentStack<Transaction>.find(key: String): String? {
    val (head, tail) = pop()
    head?.let { it.state[key]?.let { return it } }
    if (tail is PersistentStack.Empty) return null
    return tail.find(key)
}



