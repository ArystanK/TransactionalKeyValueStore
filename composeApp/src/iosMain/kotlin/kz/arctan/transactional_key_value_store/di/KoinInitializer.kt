package kz.arctan.transactional_key_value_store.di

import org.koin.core.context.startKoin

actual class KoinInitializer {

    actual fun init() {
        startKoin {
            modules(appModule, viewModelModule)
        }
    }
}
