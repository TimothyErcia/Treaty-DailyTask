package com.treaty.dailytask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepository
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class TaskGroupViewModel(private val taskGroupRepository: TaskGroupRepository) : ViewModel() {

    private val _taskGroup = MutableStateFlow<List<TaskGroupModel>>(emptyList())
    val taskGroup =
        _taskGroup
            .onStart { emitAll(getAllTaskGroup()) }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _insertResultMessage = MutableStateFlow("")
    val insertResultMessage = _insertResultMessage.asStateFlow()

    private suspend fun insertOrUpdateTaskGroup(taskGroupModel: TaskGroupModel, newData: TaskGroupModel) {
        val list =
            taskGroupModel.taskModelList.plus(newData.taskModelList)
                .map { TaskObject(it.price, it.dateAdded) }
                .toRealmList()

        val insertResult = withContext(Dispatchers.IO) {
            taskGroupRepository.insertOrUpdate(
                TaskGroupObject(
                    taskGroupModel.categoryID, list, taskGroupModel.backgroundColor))
        }

        val message = insertResult.getOrDefault("Error Message")
        _insertResultMessage.value = message
    }

    private suspend fun getAllTaskGroup(): Flow<List<TaskGroupModel>> {
        return taskGroupRepository
                .getAllTaskGroup()
                .flowOn(Dispatchers.IO)
                .mapLatest { mapToTaskGroup(it) }
    }

    private fun parseDate(date: String): String {
        val localDate = LocalDateTime.parse(date)
        return localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm "))
    }

    private fun getTotalPrice(taskModelList: List<TaskModel>): Int =
        taskModelList.sumOf { task -> task.price }

    private fun getLastUpdate(taskModelList: List<TaskModel>) =
        taskModelList.firstOrNull()?.dateAdded ?: ""

    private fun parseTaskGroupObject(taskModelList: List<TaskObject>): List<TaskModel> {
        return taskModelList.map { data ->
            TaskModel(data.taskId.toString(), data.price, data.dateAdded)
        }
    }

    fun getTotalSum(data: List<TaskGroupModel>): Int {
        if (data.isEmpty()) {
            return 0
        }

        return data.sumOf { it.totalPrice }
    }

    suspend fun getCategoryAndInsert(newData: TaskGroupModel) {
        val categoryRes = withContext(Dispatchers.IO) {
            taskGroupRepository
                .getAllTaskGroupByCategory(newData.categoryID)
                .flowOn(Dispatchers.IO)
                .mapLatest {
                    if(it.isNotEmpty()) {
                        mapToTaskGroup(it)
                    } else {
                        emptyList()
                    }
                }.stateIn(viewModelScope)
        }

        insertOrUpdateTaskGroup(categoryRes.value[0], newData)
    }

    fun createNewTask(price: String): Result<TaskModel> {
        if (price.isEmpty()) {
            return Result.failure(Throwable("Error message"))
        }

        if (price.toInt() <= 0) {
            return Result.failure(Throwable("Error message"))
        }

        val successfulTask =
            TaskModel(price = price.toInt(), dateAdded = LocalDateTime.now().toString())
        return Result.success(successfulTask)
    }

    fun createTaskGroup(
        category: String,
        currentTaskList: List<TaskModel>,
        backgroundColor: Int
    ): Result<TaskGroupModel> {
        if (currentTaskList.isEmpty()) {
            return Result.failure(Throwable("Error Message"))
        }

        if (category.isEmpty()) {
            return Result.failure(Throwable("Error Message"))
        }

        val taskGroup =
            TaskGroupModel(
                categoryID = category,
                taskModelList = currentTaskList,
                backgroundColor = backgroundColor)
        return Result.success(taskGroup)
    }

    private fun mapToTaskGroup(data: List<TaskGroupObject>): List<TaskGroupModel> {
        return data.map { taskGroup ->
            val taskModelList = parseTaskGroupObject(taskGroup.taskModelList)
            TaskGroupModel(
                taskGroup.categoryID,
                taskModelList,
                getTotalPrice(taskModelList),
                parseDate(getLastUpdate(taskModelList)),
                taskGroup.backgroundColor)
        }
    }
}
