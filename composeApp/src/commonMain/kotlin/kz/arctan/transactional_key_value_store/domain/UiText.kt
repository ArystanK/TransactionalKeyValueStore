package kz.arctan.transactional_key_value_store.domain

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface UiText {
    data class Res(val id: StringResource) : UiText

    data class Str(val string: String) : UiText

    data class Combined(val uiTextList: List<UiText>) : UiText

    @Composable
    fun toText(): String = when (this) {
        is Res -> stringResource(id)
        is Str -> string
        is Combined -> uiTextList.map { it.toText() }.joinToString("")
    }
}