package com.treaty.dailytask.repository.taskgroup

import com.treaty.dailytask.model.Task
import com.treaty.dailytask.model.TaskGroup
import kotlinx.coroutines.flow.Flow

interface TaskGroupRepository {
    suspend fun insert(task: TaskGroup)

    suspend fun getAllTaskGroup(): Flow<List<TaskGroup>>
}