package com.treaty.dailytask.di

import android.content.Context
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module
import org.koin.test.verify.verify

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
        startKoin { modules(appModuleDependency) }
    }

    @After
    fun after(){
        stopKoin()
    }

    @Test
    fun checkModules() {
        appModuleDependency.verify(
            listOf(
                Context::class
            )
        )
    }
}
