package kz.arctan.transactional_key_value_store

import androidx.compose.ui.window.ComposeUIViewController
import kz.arctan.transactional_key_value_store.di.KoinInitializer
import kz.arctan.transactional_key_value_store.presentation.App

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
    }
) {
    App()
}
