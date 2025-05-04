package com.treaty.dailytask.repository.taskgroup

import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskGroupRepositoryImpl(private val taskGroupDAO: TaskGroupRepository) {
    suspend fun insertOrUpdate(taskGroupModel: TaskGroupModel): Result<String> {
        val list = mapToTaskObject(taskGroupModel.taskModelList)

        if (list.isEmpty()) {
            return Result.failure(Throwable("Error Message"))
        }
        val totalPrice = getTotalPrice(taskGroupModel.taskModelList)
        return taskGroupDAO.insertOrUpdate(
            TaskGroupObject(
                taskGroupModel.categoryID,
                list,
                taskGroupModel.backgroundColor,
                totalPrice
            )
        )
    }

    suspend fun getAllTaskGroup(): Flow<List<TaskGroupModel>> {
        return taskGroupDAO.getAllTaskGroup().map { mapToTaskGroupModel(it) }.flowOn(Dispatchers.IO)
    }

    suspend fun updateByCategory(categoryId: String, newData: TaskModel): Result<String> {
        if (categoryId.isEmpty()) {
            return Result.failure(Throwable("Error Message"))
        }

        val mappedData = TaskObject(newData.price, newData.dateAdded)
        return taskGroupDAO.updateByCategory(categoryId, mappedData)
    }

    suspend fun deleteByCategory(categoryId: String): Result<String> {
        if (categoryId.isEmpty()) {
            return Result.failure(Throwable("Error Message"))
        }
        return taskGroupDAO.deleteByCategory(categoryId)
    }

    private fun mapToTaskGroupModel(data: List<TaskGroupObject>): List<TaskGroupModel> {
        return data.map { taskGroup ->
            val taskModelList = parseTaskGroupObject(taskGroup.taskModelList)
            TaskGroupModel(
                taskGroup.categoryID,
                taskModelList,
                taskGroup.totalPrice,
                parseDate(getLastUpdate(taskModelList)),
                taskGroup.backgroundColor,
                getLastPrice(taskModelList))
        }
    }

    private fun parseDate(date: String): String {
        val localDate = LocalDateTime.parse(date)
        return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    fun getTotalPrice(taskModelList: List<TaskModel>): Int =
        taskModelList.sumOf { task -> task.price }

    private fun getLastUpdate(taskModelList: List<TaskModel>) = taskModelList.last().dateAdded

    private fun parseTaskGroupObject(taskModelList: List<TaskObject>): List<TaskModel> {
        return taskModelList.map { data ->
            TaskModel(data.taskId.toString(), data.price, data.dateAdded)
        }
    }

    private fun mapToTaskObject(taskModelList: List<TaskModel>): RealmList<TaskObject> {
        return taskModelList.map { TaskObject(it.price, it.dateAdded) }.toRealmList()
    }

    suspend fun deleteAll(): Result<String> {
        return taskGroupDAO.deleteAllTaskGroup()
    }

    private fun getLastPrice(taskModelList: List<TaskModel>): Int = taskModelList.last().price

    suspend fun updateCategoryTotal(categoryId: String, newTotal: Int): Result<String> {
        return taskGroupDAO.updateTotal(categoryId, newTotal)
    }
}
