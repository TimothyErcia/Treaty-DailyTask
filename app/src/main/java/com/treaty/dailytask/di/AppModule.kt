package com.treaty.dailytask.di

import com.treaty.dailytask.model.Reminder
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.repository.taskgroup.TaskGroupDAO
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepositoryImpl
import com.treaty.dailytask.utility.AlarmUtility
import com.treaty.dailytask.utility.NetworkUtility
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { NetworkUtility(androidContext()) }
    single {
        val config = RealmConfiguration.Builder(
            schema = setOf(
                TaskObject::class,
                TaskGroupObject::class,
                Reminder::class
            )
        )
            .compactOnLaunch()
            .build()
        Realm.open(config)
    }
    single { AlarmUtility(androidContext()) }
    single { TaskGroupRepositoryImpl(get<TaskGroupDAO>()) }
}

val repositoryModule = module {
    single { TaskGroupDAO(get()) }
}

val viewModelModule = module {
    viewModel {
        TaskGroupViewModel(get<TaskGroupRepositoryImpl>())
    }
}