package kz.arctan.transactional_key_value_store.domain.use_case

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kz.arctan.transactional_key_value_store.domain.OperationModel

object InvalidKeyOrValue
class ParseCommandUseCase {
    operator fun invoke(
        command: String,
        key: String = "",
        value: String = ""
    ): Either<InvalidKeyOrValue, OperationModel> {
        return when (command) {
            "SET" -> {
                if (key.isBlank() || key.isEmpty() || value.isBlank() || value.isEmpty()) return InvalidKeyOrValue.left()
                OperationModel.Set(key, value).right()
            }

            "GET" -> {
                if (key.isBlank() || key.isEmpty()) return InvalidKeyOrValue.left()
                OperationModel.Get(key).right()
            }

            "DELETE" -> {
                if (key.isBlank() || key.isEmpty()) return InvalidKeyOrValue.left()
                OperationModel.Delete(key).right()
            }

            "COUNT" -> {
                if (value.isBlank() || value.isEmpty()) return InvalidKeyOrValue.left()
                OperationModel.Count(value).right()
            }

            "BEGIN" -> OperationModel.Begin.right()

            "COMMIT" -> OperationModel.Commit.right()

            "ROLLBACK" -> OperationModel.Rollback.right()

            else -> InvalidKeyOrValue.left()
        }
    }

}