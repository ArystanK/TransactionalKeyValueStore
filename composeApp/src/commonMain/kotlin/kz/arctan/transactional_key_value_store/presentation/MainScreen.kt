package kz.arctan.transactional_key_value_store.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import transactionalkeyvaluestore.composeapp.generated.resources.*

@Composable
fun MainScreen(state: ApplicationState, reduce: (ApplicationIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val lazyListState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier.weight(0.25f)
            ) {
                TextButton(onClick = {
                    reduce(ApplicationIntent.ShowDropDownMenu)
                }) {
                    Text(text = state.selectedCommand.toText())
                }
                DropdownMenu(
                    expanded = state.isExpandedDropDownMenu,
                    onDismissRequest = { reduce(ApplicationIntent.DismissDropDownMenuIntent) }
                ) {
                    OperationOptions.options.forEach { option ->
                        val optionString = stringResource(option)
                        DropdownMenuItem(onClick = { reduce(ApplicationIntent.DropDownMenuItemClickIntent(optionString)) }) {
                            Text(text = optionString)
                        }
                    }
                }
            }
            val selectedCommand = state.selectedCommand.toText()
            if (selectedCommand in setOf(
                    stringResource(Res.string.set),
                    stringResource(Res.string.get),
                    stringResource(Res.string.delete)
                )
            ) TextField(
                value = state.commandKey,
                onValueChange = { reduce(ApplicationIntent.CommandKeyChangeIntent(it)) },
                placeholder = { Text(stringResource(Res.string.key)) },
                modifier = Modifier.weight(0.25f)
            )
            if (selectedCommand in setOf(
                    stringResource(Res.string.set),
                    stringResource(Res.string.count),
                )
            ) TextField(
                value = state.commandValue,
                onValueChange = { reduce(ApplicationIntent.CommandValueChangeIntent(it)) },
                placeholder = { Text(stringResource(Res.string.value)) },
                modifier = Modifier.weight(0.25f)
            )
            TextButton(
                onClick = {
                    reduce(ApplicationIntent.ExecuteOperationIntent(selectedCommand))
                    scope.launch { lazyListState.animateScrollToItem(state.outputs.lastIndex.coerceAtLeast(0)) }
                },
                modifier = Modifier.weight(0.25f)
            ) {
                Text(stringResource(Res.string.execute))
            }
        }
        LazyColumn(state = lazyListState) {
            items(state.outputs) { output ->
                Text(text = output.toUiText().toText())
            }
        }
    }
}
