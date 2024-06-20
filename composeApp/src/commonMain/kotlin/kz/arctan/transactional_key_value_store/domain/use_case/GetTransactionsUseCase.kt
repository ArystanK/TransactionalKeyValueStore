package kz.arctan.transactional_key_value_store.domain.use_case

import kz.arctan.transactional_key_value_store.domain.OperationModel
import kz.arctan.transactional_key_value_store.domain.PersistentStack
import kz.arctan.transactional_key_value_store.domain.Transaction
import kz.arctan.transactional_key_value_store.domain.stackOf
import kz.arctan.transactional_key_value_store.domain.updateLast

class GetTransactionsUseCase {
    operator fun invoke(operations: List<OperationModel>): PersistentStack<Transaction> {
        var transactionModel = stackOf(Transaction())
        operations.forEach { operation ->
            when (operation) {
                OperationModel.Begin -> transactionModel = transactionModel.push(
                    Transaction(
                        transactionModel.peek()?.state ?: emptyMap()
                    )
                )

                OperationModel.Commit -> transactionModel =
                    transactionModel.pop().let { (last, previousTransactions) ->
                        val (current, beforePrevTransactions) = previousTransactions.pop()
                        beforePrevTransactions.push(last ?: current ?: Transaction())
                    }

                OperationModel.Rollback -> transactionModel = transactionModel.pop().second

                is OperationModel.Delete -> transactionModel = transactionModel.updateLast {
                    it.copy(state = it.state - operation.key)
                }

                is OperationModel.Set -> transactionModel = transactionModel.updateLast {
                    it.copy(state = it.state + (operation.key to operation.value))
                }

                is OperationModel.Count, is OperationModel.Get -> Unit
            }
        }
        return transactionModel
    }
}