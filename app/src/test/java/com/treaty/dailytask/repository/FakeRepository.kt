package com.treaty.dailytask.repository

import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepository
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime

class FakeRepository {
    class FakeRepositoryDAO : TaskGroupRepository {
        private val flow = MutableStateFlow<List<TaskGroupObject>>(emptyList())
        private var listTaskGroup = listOf(
            TaskGroupObject("Food", realmListOf(TaskObject(100, LocalDateTime.now().plusMinutes(1).toString())), 0),
            TaskGroupObject("Food", realmListOf(TaskObject(100, LocalDateTime.now().plusMinutes(2).toString())), 0),
            TaskGroupObject("Transportation", realmListOf(TaskObject(100, LocalDateTime.now().plusMinutes(2).toString())), 0)
        )
        override suspend fun insertOrUpdate(taskGroupObject: TaskGroupObject): Result<String> {
            listTaskGroup = listTaskGroup.plus(taskGroupObject)
            flow.emit(listTaskGroup)
            return Result.success("Successfully Added")
        }

        override suspend fun getAllTaskGroup(): Flow<List<TaskGroupObject>> {
            flow.emit(listTaskGroup)
            return flow
        }

        override suspend fun updateByCategory(
            categoryId: String,
            newData: TaskObject
        ): Result<String> {
            return Result.success("Successfully Added")
        }

        override suspend fun deleteByCategory(categoryId: String): Result<String> {
            return Result.success("Category Removed")
        }

        override suspend fun deleteAllTaskGroup(): Result<String> {
            return Result.success("Successfully removed All")
        }
    }
}
