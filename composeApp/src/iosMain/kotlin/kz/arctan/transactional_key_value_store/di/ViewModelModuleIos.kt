package kz.arctan.transactional_key_value_store.di

import kz.arctan.transactional_key_value_store.presentation.MainViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val viewModelModule: Module = module {
    singleOf(::MainViewModel)
}