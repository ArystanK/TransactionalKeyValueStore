package kz.arctan.transactional_key_value_store.domain

data class Transaction(
    val state: Map<String, String> = emptyMap()
)
