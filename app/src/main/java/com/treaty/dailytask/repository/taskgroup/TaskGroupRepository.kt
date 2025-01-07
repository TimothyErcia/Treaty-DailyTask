package com.treaty.dailytask.repository.taskgroup

import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import kotlinx.coroutines.flow.Flow

interface TaskGroupRepository {
    suspend fun insertOrUpdate(taskGroupObject: TaskGroupObject): Result<String>

    suspend fun getAllTaskGroup(): Flow<List<TaskGroupObject>>

    suspend fun updateByCategory(categoryId: String, newData: TaskObject): Result<String>

    suspend fun deleteByCategory(categoryId: String): Result<String>

    suspend fun deleteAllTaskGroup(): Result<String>
}