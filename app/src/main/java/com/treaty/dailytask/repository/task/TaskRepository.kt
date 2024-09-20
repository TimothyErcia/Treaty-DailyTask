package com.treaty.dailytask.repository.task

import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insert(taskObject: TaskObject)

    suspend fun getAllTask(): Flow<List<TaskObject>>
}