package kz.arctan.transactional_key_value_store.presentation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kz.arctan.transactional_key_value_store.domain.*
import org.jetbrains.compose.resources.StringResource
import transactionalkeyvaluestore.composeapp.generated.resources.*

data class ApplicationState(
    val outputs: PersistentList<Output> = persistentListOf(),
    val transactions: PersistentStack<Transaction> = stackOf(Transaction()),
    val isExpandedDropDownMenu: Boolean = false,
    val selectedCommand: UiText = UiText.Res(Res.string.set),
    val commandValue: String = "",
    val commandKey: String = "",
    val numberOfTransactions: Int = 0
)

data object OperationOptions {
    val options: ImmutableList<StringResource> = persistentListOf(
        Res.string.get,
        Res.string.set,
        Res.string.delete,
        Res.string.count,
        Res.string.begin,
        Res.string.commit,
        Res.string.rollback
    )
}
