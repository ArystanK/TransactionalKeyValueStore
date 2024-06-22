package kz.arctan.transactional_key_value_store.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kz.arctan.transactional_key_value_store.domain.*
import org.jetbrains.compose.resources.StringResource
import transactionalkeyvaluestore.composeapp.generated.resources.*

@Stable
data class ApplicationState(
    @Stable val outputs: PersistentList<Output> = persistentListOf(),
    @Stable val transactions: PersistentStack<Transaction> = stackOf(Transaction()),
    @Stable val isExpandedDropDownMenu: Boolean = false,
    @Stable val selectedCommand: UiText = UiText.Res(Res.string.set),
    @Stable val commandValue: String = "",
    @Stable val commandKey: String = "",
    @Stable val numberOfTransactions: Int = 0
)

@Immutable
object OperationOptions {
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
