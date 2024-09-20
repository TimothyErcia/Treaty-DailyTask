package com.treaty.dailytask.di

import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.repository.task.TaskRepository
import com.treaty.dailytask.repository.task.TaskRepositoryImpl
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepository
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepositoryImpl
import com.treaty.dailytask.utility.NetworkUtility
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import com.treaty.dailytask.viewmodel.TaskViewModel
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
                TaskGroupObject::class
            )
        )
            .compactOnLaunch()
            .build()
        Realm.open(config)
    }
}

val repositoryModule = module {
    single<TaskRepository> {
        TaskRepositoryImpl(get())
    }

    single<TaskGroupRepository> {
        TaskGroupRepositoryImpl(get())
    }
}

val taskAppModule = module {
    viewModel {
        TaskViewModel(get())
    }
}

val taskGroupModule = module {
    viewModel {
        TaskGroupViewModel(get())
    }
}