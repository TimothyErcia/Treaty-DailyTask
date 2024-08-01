package com.treaty.dailytask.di

import com.treaty.dailytask.utility.NetworkUtility
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { NetworkUtility(androidContext()) }
}