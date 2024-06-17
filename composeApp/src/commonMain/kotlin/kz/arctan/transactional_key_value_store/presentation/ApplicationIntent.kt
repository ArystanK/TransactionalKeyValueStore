package kz.arctan.transactional_key_value_store.presentation

sealed interface ApplicationIntent {
    data object DismissDropDownMenuIntent : ApplicationIntent
    data object ShowDropDownMenu : ApplicationIntent
    data class CommandValueChangeIntent(val value: String) : ApplicationIntent
    data class CommandKeyChangeIntent(val key: String) : ApplicationIntent
    data class ExecuteOperationIntent(val command: String) : ApplicationIntent
    data class DropDownMenuItemClickIntent(val operation: String) : ApplicationIntent
}
