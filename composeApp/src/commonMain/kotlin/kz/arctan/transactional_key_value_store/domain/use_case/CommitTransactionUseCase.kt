package kz.arctan.transactional_key_value_store.domain.use_case

import kz.arctan.transactional_key_value_store.domain.PersistentStack
import kz.arctan.transactional_key_value_store.domain.Transaction
import kz.arctan.transactional_key_value_store.domain.updateLast

class CommitTransactionUseCase {
    operator fun invoke(transactions: PersistentStack<Transaction>): PersistentStack<Transaction> {
        val (last, previousTransactions) = transactions.pop()
        return previousTransactions
            .updateLast { transaction ->
                transaction.copy(
                    state = last?.state ?: transaction.state
                )
            }
    }
}