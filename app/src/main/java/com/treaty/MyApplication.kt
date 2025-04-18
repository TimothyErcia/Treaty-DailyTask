package com.treaty

import android.app.Application
import com.treaty.dailytask.di.appModule
import com.treaty.dailytask.di.repositoryModule
import com.treaty.dailytask.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(level = Level.INFO)
            androidContext(this@MyApplication)
            modules(
                appModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}