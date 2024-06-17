package kz.arctan.transactional_key_value_store

import android.app.Application
import kz.arctan.transactional_key_value_store.di.KoinInitializer

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
    }
}