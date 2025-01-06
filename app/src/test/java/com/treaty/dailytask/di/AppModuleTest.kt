package com.treaty.dailytask.di

import android.content.Context
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module
import org.koin.test.verify.verify

@OptIn(ExperimentalCoroutinesApi::class)
class AppModuleTest {

    val appModuleDependency = module {
        includes(
            appModule,
            repositoryModule
        )
        viewModelOf(::TaskGroupViewModel)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        startKoin { modules(appModuleDependency) }
    }

    @After
    fun after(){
        Dispatchers.resetMain()
        stopKoin()
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkModules() = runTest {
        appModuleDependency.verify(
            listOf(
                Context::class,
            )
        )
    }
}
