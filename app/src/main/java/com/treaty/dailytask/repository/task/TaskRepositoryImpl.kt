package com.treaty.dailytask.repository.task

import android.util.Log
import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import io.realm.kotlin.Realm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TaskRepositoryImpl(private val realm: Realm) : TaskRepository {

    override suspend fun insert(taskObject: TaskObject) {
        realm.write {
            copyToRealm(taskObject)
        }
    }

    override suspend fun getAllTask(): Flow<List<TaskObject>> {
        val realmQuery = realm.query(TaskObject::class).find()
        val taskObjectFlow = realmQuery.asFlow()
        if(realmQuery.isNotEmpty()) {
            Log.d("REALM", "getAllTask: ${taskObjectFlow.first().list.get(0).taskId}")
            Log.d("REALM", "getAllTask: ${taskObjectFlow.first().list.get(0).dateAdded}")
            Log.d("REALM", "getAllTask: ${taskObjectFlow.first().list.get(0).price}")
        }
        return flow { taskObjectFlow.collect { emit(it.list) } }
    }
}