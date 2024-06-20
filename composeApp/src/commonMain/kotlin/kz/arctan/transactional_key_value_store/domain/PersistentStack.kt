package kz.arctan.transactional_key_value_store.domain

sealed class PersistentStack<out T> {
    internal class Empty<T> : PersistentStack<T>() {
        override fun equals(other: Any?): Boolean {
            if (other is Empty<*>) return true
            return super.equals(other)
        }

        override fun hashCode(): Int = 0
    }

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
    val (last, previous) = pop()
    return last?.let { previous.push(transform(it)) } ?: this
}

tailrec fun <T> PersistentStack<T>.forEach(f: (T) -> Unit) {
    val (head, tail) = pop()
    if (head == null) return
    f(head)
    return tail.forEach(f)
}

tailrec fun <T, R> PersistentStack<T>.map(
    f: (T) -> R,
    result: PersistentStack<R> = emptyStack()
): PersistentStack<R> {
    val (head, tail) = pop()
    if (head == null) return result
    return tail.map(f, result.push(f(head)))
}

