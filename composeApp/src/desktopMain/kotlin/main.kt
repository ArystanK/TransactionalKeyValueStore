import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kz.arctan.transactional_key_value_store.di.KoinInitializer
import kz.arctan.transactional_key_value_store.presentation.App

fun main() {
    KoinInitializer().init()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Transactional Key Value Store",
        ) {
            App()
        }
    }
}