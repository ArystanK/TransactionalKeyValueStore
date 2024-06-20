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
