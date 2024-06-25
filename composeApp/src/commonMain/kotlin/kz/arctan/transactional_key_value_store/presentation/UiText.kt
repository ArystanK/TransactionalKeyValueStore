package kz.arctan.transactional_key_value_store.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Stable
sealed interface UiText {
    data class Res(val id: StringResource) : UiText

    data class Str(val string: String) : UiText

    data class Combined(val uiTextList: List<UiText>) : UiText
}

@Composable
fun UiText.toText(): String = when (this) {
    is UiText.Res -> stringResource(id)
    is UiText.Str -> string
    is UiText.Combined -> uiTextList.map { it.toText() }.joinToString("")
}
