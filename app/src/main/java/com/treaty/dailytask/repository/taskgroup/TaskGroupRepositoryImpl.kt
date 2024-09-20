package com.treaty.dailytask.repository.taskgroup

import android.util.Log
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import io.realm.kotlin.Realm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TaskGroupRepositoryImpl(private val realm: Realm) : TaskGroupRepository {
    override suspend fun insert(taskGroupObject: TaskGroupObject) {
        realm.write {
            copyToRealm(taskGroupObject)
        }
    }

    override suspend fun getAllTaskGroup(): Flow<List<TaskGroupObject>> {
        val realmQuery = realm.query(TaskGroupObject::class).find()
        val realmFlow = realmQuery.asFlow()
        if(realmQuery.isNotEmpty()) {
            Log.d("REALM", "taskgroup UUID: ${realmFlow.first().list.get(0).taskGroupUUID}")
            Log.d("REALM", "categoryID: ${realmFlow.first().list.get(0).categoryID}")
            Log.d("REALM", "getTask: ${realmFlow.first().list.get(0).taskModelList.get(0).dateAdded}")
            Log.d("REALM", "getTask: ${realmFlow.first().list.get(0).taskModelList.get(0).price}")
            Log.d("REALM", "getTask: ${realmFlow.first().list.get(0).taskModelList.get(0).taskId}")
        }
        return flow { realmFlow.collect { data -> emit(data.list) } }
    }

}