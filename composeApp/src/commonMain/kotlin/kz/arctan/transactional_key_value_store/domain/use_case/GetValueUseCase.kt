package kz.arctan.transactional_key_value_store.domain.use_case

import kz.arctan.transactional_key_value_store.domain.PersistentStack
import kz.arctan.transactional_key_value_store.domain.Transaction

class GetValueUseCase {
    operator fun invoke(key: String, transactions: PersistentStack<Transaction>): String? =
        transactions.find { it.state.containsKey(key) }?.state?.get(key)
}

tailrec fun <T> PersistentStack<T>.find(predicate: (T) -> Boolean): T? {
    val (head, tail) = pop()
    head?.let { if (predicate(it)) return it }
    if (tail is PersistentStack.Empty) return null
    return tail.find(predicate)
}
