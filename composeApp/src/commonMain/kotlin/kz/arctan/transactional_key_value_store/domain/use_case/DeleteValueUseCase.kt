package kz.arctan.transactional_key_value_store.domain.use_case

import kz.arctan.transactional_key_value_store.domain.PersistentStack
import kz.arctan.transactional_key_value_store.domain.Transaction

class DeleteValueUseCase {
    operator fun invoke(
        transactions: PersistentStack<Transaction>,
        key: String
    ): PersistentStack<Transaction> = transactions.delete(key)
}

fun PersistentStack<Transaction>.delete(key: String): PersistentStack<Transaction> {
    var stack = this
    val removedTransactions = ArrayDeque<Transaction>()
    while (!stack.isEmpty()) {
        val (head, tail) = stack.pop()
        if (head != null) {
            if (head.state.containsKey(key)) {
                stack = stack.push(head.copy(state = head.state - key))
                break
            }
            removedTransactions.addLast(head)
        }
        stack = tail
    }
    removedTransactions.forEach { stack.push(it) }
    return stack
}
