package kz.arctan.transactional_key_value_store.presentation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kz.arctan.transactional_key_value_store.di.koinViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val viewModel = koinViewModel<MainViewModel>()
            val state by viewModel.uiState.collectAsState()
            MainScreen(state = state, reduce = viewModel::reduce)
        }
    }
}


