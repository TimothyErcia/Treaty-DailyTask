package com.treaty.dailytask.repository.taskgroup

import com.treaty.dailytask.model.TaskGroup
import io.realm.kotlin.Realm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskGroupRepositoryImpl(private val realm: Realm) : TaskGroupRepository {
    override suspend fun insert(taskGroup: TaskGroup) {
        realm.write {
            copyToRealm(taskGroup)
        }
    }

    override suspend fun getAllTaskGroup(): Flow<List<TaskGroup>> {
        realm.query(TaskGroup::class).find().asFlow()

        return flow { emit(emptyList()) }
    }

}