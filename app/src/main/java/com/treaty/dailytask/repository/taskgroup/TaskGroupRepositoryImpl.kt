package com.treaty.dailytask.repository.taskgroup

import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskGroupRepositoryImpl(private val taskGroupDAO: TaskGroupRepository) {
    suspend fun insertOrUpdate(taskGroupModel: TaskGroupModel): Result<String> {
        val list =
            taskGroupModel.taskModelList
                .map { TaskObject(it.price, it.dateAdded) }
                .toRealmList()

        if(list.isEmpty()) {
            return Result.failure(Throwable("Error Message"))
        }
        return taskGroupDAO.insertOrUpdate(TaskGroupObject(taskGroupModel.categoryID, list, taskGroupModel.backgroundColor))
    }

    suspend fun getAllTaskGroup(): Flow<List<TaskGroupModel>> {
        return taskGroupDAO.getAllTaskGroup()
            .flowOn(Dispatchers.IO)
            .map{ mapToTaskGroup(it) }
    }

    suspend fun updateByCategory(categoryId: String, newData: TaskModel): Result<String> {
        if(categoryId.isEmpty()) {
            return Result.failure(Throwable("Error Message"))
        }

        val mappedData = TaskObject(newData.price, newData.dateAdded)
        return taskGroupDAO.updateByCategory(categoryId, mappedData)
    }
    suspend fun deleteByCategory(categoryId: String): Result<String> {
        if(categoryId.isEmpty()) {
            return Result.failure(Throwable("Error Message"))
        }
        return taskGroupDAO.deleteByCategory(categoryId)
    }
    private fun mapToTaskGroup(data: List<TaskGroupObject>): List<TaskGroupModel> {
        return data.map { taskGroup ->
            val taskModelList = parseTaskGroupObject(taskGroup.taskModelList)
            TaskGroupModel(
                taskGroup.categoryID,
                taskModelList,
                getTotalPrice(taskModelList),
                parseDate(getLastUpdate(taskModelList)),
                taskGroup.backgroundColor,
                getLastPrice(taskModelList))
        }
    }

    private fun parseDate(date: String): String {
        val localDate = LocalDateTime.parse(date)
        return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    private fun getTotalPrice(taskModelList: List<TaskModel>): Int =
        taskModelList.sumOf { task -> task.price }

    private fun getLastUpdate(taskModelList: List<TaskModel>) =
        taskModelList.last().dateAdded

    private fun parseTaskGroupObject(taskModelList: List<TaskObject>): List<TaskModel> {
        return taskModelList.map { data ->
            TaskModel(data.taskId.toString(), data.price, data.dateAdded)
        }
    }

    suspend fun deleteAll(): Result<String> {
        return taskGroupDAO.deleteAllTaskGroup()
    }

    private fun getLastPrice(taskModelList: List<TaskModel>): Int =
        taskModelList.last().price
}