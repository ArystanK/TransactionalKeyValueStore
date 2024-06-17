package kz.arctan.transactional_key_value_store.di

import kz.arctan.transactional_key_value_store.presentation.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.module.Module

actual val viewModelModule: Module = module {
    viewModelOf(::MainViewModel)
}
