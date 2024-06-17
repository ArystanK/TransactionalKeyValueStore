package kz.arctan.transactional_key_value_store.domain

sealed class PersistentStack<out T> {
    internal class Empty<T> : PersistentStack<T>()
    internal data class Node<T>(
        val head: T,
        val tail: PersistentStack<T>
    ) : PersistentStack<T>()

    fun isEmpty() = this is Empty

    fun peek(): T? = when (this) {
        is Empty -> null
        is Node -> head
    }

    fun pop(): Pair<T?, PersistentStack<T>> = when (this) {
        is Empty -> null to Empty()
        is Node -> head to tail
    }

    fun push(value: @UnsafeVariance T): PersistentStack<T> = Node(value, this)
}

fun <T> emptyStack(): PersistentStack<T> = PersistentStack.Empty()

fun <T> stackOf(vararg elements: T): PersistentStack<T> {
    var stack = emptyStack<T>()
    elements.forEach { stack = stack.push(it) }
    return stack
}

fun <T> PersistentStack<T>.updateLast(transform: (T) -> T): PersistentStack<T> {
    val (last, previousTransactions) = pop()
    return last?.let { previousTransactions.push(transform(it)) } ?: this
}
