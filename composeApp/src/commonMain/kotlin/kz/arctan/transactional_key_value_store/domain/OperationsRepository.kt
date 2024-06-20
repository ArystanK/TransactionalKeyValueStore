package kz.arctan.transactional_key_value_store.domain

interface OperationsRepository {
    suspend fun addOperation(operationModel: OperationModel): Result<Boolean>
    suspend fun getAll(): Result<List<OperationModel>>
}
