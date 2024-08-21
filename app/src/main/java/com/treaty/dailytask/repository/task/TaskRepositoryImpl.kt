package com.treaty.dailytask.repository.task

import android.util.Log
import com.treaty.dailytask.model.Task
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.find
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

class TaskRepositoryImpl(private val realm: Realm) : TaskRepository {

    override suspend fun insert(task: Task) {
        realm.write {
            copyToRealm(task)
        }
    }

    override suspend fun getAllTask(): Flow<List<Task>> {
        val listData: List<Task> = realm.query(Task::class).find()
        Log.d("REALM", "getAllTask: ${listData.get(0)}")
        Log.d("REALM", "getAllTask: ${listData.get(0).price}")
        Log.d("REALM", "getAllTask: ${listData.get(0).dateAdded}")
        return flow { emit(realm.query(Task::class).find()) }
    }
}