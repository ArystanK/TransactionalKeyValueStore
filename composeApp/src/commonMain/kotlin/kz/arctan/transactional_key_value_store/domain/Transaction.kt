package kz.arctan.transactional_key_value_store.domain

data class Transaction(
    val state: Map<String, String> = emptyMap()
)

fun PersistentStack<Transaction>.updateLast(transform: (Transaction) -> Transaction): PersistentStack<Transaction> {
    val (last, previousTransactions) = pop()
    return last?.let { previousTransactions.push(transform(it)) } ?: this
}
