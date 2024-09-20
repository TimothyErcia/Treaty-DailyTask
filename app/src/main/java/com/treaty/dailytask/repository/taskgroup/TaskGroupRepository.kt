package com.treaty.dailytask.repository.taskgroup

import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface TaskGroupRepository {
    suspend fun insert(taskGroupObject: TaskGroupObject)

    suspend fun getAllTaskGroup(): Flow<List<TaskGroupObject>>
}