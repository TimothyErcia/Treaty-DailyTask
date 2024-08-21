package com.treaty.dailytask.repository.task

import com.treaty.dailytask.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insert(task: Task)

    suspend fun getAllTask(): Flow<List<Task>>
}