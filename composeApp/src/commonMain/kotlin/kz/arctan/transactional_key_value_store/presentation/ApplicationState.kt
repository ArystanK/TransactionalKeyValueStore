package kz.arctan.transactional_key_value_store.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kz.arctan.transactional_key_value_store.domain.*
import transactionalkeyvaluestore.composeapp.generated.resources.*

@Stable
data class ApplicationState(
    val outputs: List<Output> = emptyList(),
    val transactions: PersistentStack<Transaction> = stackOf(Transaction()),
    val isExpandedDropDownMenu: Boolean = false,
    val selectedCommand: UiText = UiText.Res(Res.string.set),
    val commandValue: String = "",
    val commandKey: String = "",
    val numberOfTransactions: Int = 0
)

sealed interface Output {
    data class Operation(val operationModel: OperationModel) : Output
    data class Result(val result: UiText) : Output

    fun toUiText(): UiText {
        return when (this) {
            is Operation -> when (this.operationModel) {
                OperationModel.Begin -> UiText.Res(Res.string.begin)
                OperationModel.Commit -> UiText.Res(Res.string.commit)
                is OperationModel.Count -> UiText.Combined(
                    listOf(
                        UiText.Res(Res.string.count),
                        UiText.Str(" "),
                        UiText.Str(this.operationModel.value)
                    )
                )

                is OperationModel.Delete -> UiText.Combined(
                    listOf(
                        UiText.Res(Res.string.delete),
                        UiText.Str(" "),
                        UiText.Str(this.operationModel.key)
                    )
                )

                is OperationModel.Get -> UiText.Combined(
                    listOf(
                        UiText.Res(Res.string.get),
                        UiText.Str(" "),
                        UiText.Str(this.operationModel.key)
                    )
                )

                OperationModel.Rollback -> UiText.Res(Res.string.rollback)

                is OperationModel.Set -> UiText.Combined(
                    listOf(
                        UiText.Res(Res.string.set),
                        UiText.Str(" "),
                        UiText.Str(this.operationModel.key),
                        UiText.Str(" "),
                        UiText.Str(this.operationModel.value),
                    )
                )
            }

            is Result -> return this.result
        }
    }
}

@Immutable
data object OperationOptions {
    val options = listOf(
        Res.string.get,
        Res.string.set,
        Res.string.delete,
        Res.string.count,
        Res.string.begin,
        Res.string.commit,
        Res.string.rollback
    )
}
