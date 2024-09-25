package com.treaty.dailytask.repository.taskgroup

import android.util.Log
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TaskGroupRepositoryImpl(private val realm: Realm) : TaskGroupRepository {
    override suspend fun insertOrUpdate(taskGroupObject: TaskGroupObject) {
        realm.write {
            copyToRealm(
                taskGroupObject,
                UpdatePolicy.ALL
            )
        }
    }

    override suspend fun getAllTaskGroup(): Flow<List<TaskGroupObject>> {
        val realmQuery = realm.query(TaskGroupObject::class).find()
        val realmFlow = realmQuery.asFlow()

        return realmFlow.map { it.list }
    }

    override suspend fun getAllTaskGroupByCategory(categoryId: String): Flow<List<TaskGroupObject>> {
        val realmQuery = realm.query(TaskGroupObject::class, "categoryID == $0", categoryId).find()
        val realmFlow = realmQuery.asFlow()

        return realmFlow.map { it.list }
    }

}