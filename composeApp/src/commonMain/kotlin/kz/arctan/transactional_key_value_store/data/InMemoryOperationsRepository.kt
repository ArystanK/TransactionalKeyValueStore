package kz.arctan.transactional_key_value_store.data

import kz.arctan.transactional_key_value_store.domain.OperationModel
import kz.arctan.transactional_key_value_store.domain.OperationsRepository

internal class InMemoryOperationsRepository : OperationsRepository {
    private val operations: MutableList<OperationModel> = mutableListOf()

    override suspend fun getAll(): Result<List<OperationModel>> = Result.success(operations)

    override suspend fun addOperation(operationModel: OperationModel): Result<Boolean> {
        operations.add(operationModel)
        return Result.success(true)
    }
}
