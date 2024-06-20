package kz.arctan.transactional_key_value_store.presentation

import kz.arctan.transactional_key_value_store.domain.OperationModel
import transactionalkeyvaluestore.composeapp.generated.resources.Res
import transactionalkeyvaluestore.composeapp.generated.resources.begin
import transactionalkeyvaluestore.composeapp.generated.resources.commit
import transactionalkeyvaluestore.composeapp.generated.resources.count
import transactionalkeyvaluestore.composeapp.generated.resources.delete
import transactionalkeyvaluestore.composeapp.generated.resources.get
import transactionalkeyvaluestore.composeapp.generated.resources.rollback
import transactionalkeyvaluestore.composeapp.generated.resources.set

sealed interface Output {
    data class Operation(val operationModel: OperationModel, override val key: Long) : Output
    data class Result(val result: UiText, override val key: Long) : Output

    val key: Long
}

fun Output.toUiText(): UiText {
    return when (this) {
        is Output.Operation -> when (this.operationModel) {
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

        is Output.Result -> return this.result
    }
}