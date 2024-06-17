package kz.arctan.transactional_key_value_store.domain

sealed interface OperationModel {
    data class Set(val key: String, val value: String) : OperationModel
    data class Get(val key: String) : OperationModel
    data class Delete(val key: String) : OperationModel
    data class Count(val value: String) : OperationModel
    data object Begin : OperationModel
    data object Commit : OperationModel
    data object Rollback : OperationModel
}

fun OperationModel(command: String, key: String = "", value: String = ""): OperationModel? {
    return when (command) {
        "SET" -> {
            if (key.isBlank() || key.isEmpty() || value.isBlank() || value.isEmpty()) return null
            OperationModel.Set(key, value)
        }

        "GET" -> {
            if (key.isBlank() || key.isEmpty()) return null
            OperationModel.Get(key)
        }

        "DELETE" -> {
            if (key.isBlank() || key.isEmpty()) return null
            OperationModel.Delete(key)
        }

        "COUNT" -> {
            if (value.isBlank() || value.isEmpty()) return null
            OperationModel.Count(value)
        }

        "BEGIN" -> OperationModel.Begin

        "COMMIT" -> OperationModel.Commit

        "ROLLBACK" -> OperationModel.Rollback

        else -> null
    }
}

fun List<OperationModel>.toTransactions(): PersistentStack<Transaction> {
    var transactionModel = stackOf(Transaction())
    forEach { operation ->
        when (operation) {
            OperationModel.Begin -> transactionModel = transactionModel.push(
                Transaction(
                    transactionModel.peek()?.state ?: emptyMap()
                )
            )

            OperationModel.Commit -> transactionModel = transactionModel.pop().let { (last, previousTransactions) ->
                val (current, beforePrevTransactions) = previousTransactions.pop()
                beforePrevTransactions.push(last ?: current ?: Transaction())
            }

            OperationModel.Rollback -> transactionModel = transactionModel.pop().second

            is OperationModel.Delete -> transactionModel = transactionModel.updateLast {
                it.copy(state = it.state - operation.key)
            }

            is OperationModel.Count -> Unit

            is OperationModel.Get -> Unit

            is OperationModel.Set -> transactionModel = transactionModel.updateLast {
                it.copy(state = it.state + (operation.key to operation.value))
            }
        }
    }
    return transactionModel
}
