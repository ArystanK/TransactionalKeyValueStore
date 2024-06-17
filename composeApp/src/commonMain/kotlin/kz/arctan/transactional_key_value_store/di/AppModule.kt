package kz.arctan.transactional_key_value_store.di

import kz.arctan.transactional_key_value_store.data.InMemoryOperationsRepository
import kz.arctan.transactional_key_value_store.domain.OperationsRepository
import org.koin.dsl.module

val appModule = module {
    single<OperationsRepository> { InMemoryOperationsRepository() }
}
