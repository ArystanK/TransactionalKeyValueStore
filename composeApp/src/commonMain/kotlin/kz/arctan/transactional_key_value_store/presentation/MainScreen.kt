package kz.arctan.transactional_key_value_store.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import transactionalkeyvaluestore.composeapp.generated.resources.Res
import transactionalkeyvaluestore.composeapp.generated.resources.count
import transactionalkeyvaluestore.composeapp.generated.resources.delete
import transactionalkeyvaluestore.composeapp.generated.resources.execute
import transactionalkeyvaluestore.composeapp.generated.resources.get
import transactionalkeyvaluestore.composeapp.generated.resources.key
import transactionalkeyvaluestore.composeapp.generated.resources.set
import transactionalkeyvaluestore.composeapp.generated.resources.value

@Composable
fun MainScreen(state: ApplicationState, reduce: (ApplicationIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val lazyListState = rememberLazyListState()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier.weight(0.25f)
            ) {
                TextButton(onClick = { reduce(ApplicationIntent.ShowDropDownMenu) }) {
                    Text(text = state.selectedCommand.toText())
                }
                DropdownMenu(
                    expanded = state.isExpandedDropDownMenu,
                    onDismissRequest = { reduce(ApplicationIntent.DismissDropDownMenuIntent) }
                ) {
                    OperationOptions.options.forEach { option ->
                        val optionString = stringResource(option)
                        DropdownMenuItem(onClick = {
                            reduce(ApplicationIntent.DropDownMenuItemClickIntent(optionString))
                        }) {
                            Text(text = optionString)
                        }
                    }
                }
            }
            val selectedCommand = state.selectedCommand.toText()
            val set = stringResource(Res.string.set)
            val get = stringResource(Res.string.get)
            val delete = stringResource(Res.string.delete)
            val count = stringResource(Res.string.count)

            val showKeyTextField by derivedStateOf { selectedCommand in setOf(set, get, delete) }

            val showValueTextField by derivedStateOf { selectedCommand in setOf(set, count) }

            if (showKeyTextField) {
                TextField(
                    value = state.commandKey,
                    onValueChange = { reduce(ApplicationIntent.CommandKeyChangeIntent(it)) },
                    placeholder = { Text(stringResource(Res.string.key)) },
                    modifier = Modifier.weight(0.25f)
                )
            }

            if (showValueTextField) {
                TextField(
                    value = state.commandValue,
                    onValueChange = { reduce(ApplicationIntent.CommandValueChangeIntent(it)) },
                    placeholder = { Text(stringResource(Res.string.value)) },
                    modifier = Modifier.weight(0.25f)
                )
            }
            val scope = rememberCoroutineScope()

            TextButton(
                onClick = {
                    reduce(ApplicationIntent.ExecuteOperationIntent(selectedCommand))
                    scope.launch {
                        lazyListState.animateScrollToItem(
                            state.outputs.lastIndex.coerceAtLeast(0)
                        )
                    }
                },
                modifier = Modifier.weight(0.25f)
            ) {
                Text(stringResource(Res.string.execute))
            }
        }
        LazyColumn(state = lazyListState) {
            items(items = state.outputs, key = { it.key }) { output ->
                Text(text = output.toUiText().toText())
            }
        }
    }
}
