package kz.arctan.transactional_key_value_store.di

import kz.arctan.transactional_key_value_store.data.InMemoryOperationsRepository
import kz.arctan.transactional_key_value_store.domain.OperationsRepository
import kz.arctan.transactional_key_value_store.domain.use_case.CommitTransactionUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.CountValueUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.DeleteValueUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.GetTransactionsUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.GetValueUseCase
import kz.arctan.transactional_key_value_store.domain.use_case.ParseCommandUseCase
import kz.arctan.transactional_key_value_store.presentation.intent_handler.RollbackIntentHandler
import kz.arctan.transactional_key_value_store.presentation.intent_handler.SetValueIntentHandler
import org.koin.dsl.module

val appModule = module {
    single<OperationsRepository> { InMemoryOperationsRepository() }
    single { CommitTransactionUseCase() }
    single { CountValueUseCase() }
    single { DeleteValueUseCase() }
    single { GetTransactionsUseCase() }
    single { GetValueUseCase() }
    single { ParseCommandUseCase() }
    single { RollbackIntentHandler() }
    single { SetValueIntentHandler() }
}
