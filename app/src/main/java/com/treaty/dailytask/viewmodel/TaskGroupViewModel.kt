package com.treaty.dailytask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treaty.dailytask.model.Status
import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class TaskGroupViewModel(private val taskGroupRepositoryImpl: TaskGroupRepositoryImpl) :
    ViewModel() {

    private val _taskGroup = MutableStateFlow<List<TaskGroupModel>>(emptyList())
    val taskGroup =
        _taskGroup
            .onStart { emitAll(getAllTaskGroup()) }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _resultMessage = MutableStateFlow(Status())
    val resultMessage = _resultMessage.asStateFlow()

    private suspend fun insertOrUpdateTaskGroup(taskGroupModel: TaskGroupModel) {
        val insertResult =
            withContext(Dispatchers.Default) { taskGroupRepositoryImpl.insertOrUpdate(taskGroupModel) }

        setMessage(insertResult)
    }

    private suspend fun getAllTaskGroup(): Flow<List<TaskGroupModel>> {
        return taskGroupRepositoryImpl.getAllTaskGroup()
    }

    fun getTotalSum(data: List<TaskGroupModel>): Int {
        if (data.isEmpty()) {
            return 0
        }

        return data.sumOf { it.totalPrice }
    }

    suspend fun getCategoryAndInsert(categoryId: String, newData: TaskModel, backgroundColor: Int) {
        val categoryRes =
            withContext(Dispatchers.Default) {
                taskGroupRepositoryImpl.updateByCategory(categoryId, newData)
            }

        categoryRes
            .onSuccess { message -> setMessage(categoryRes) }
            .onFailure {
                val taskGroupModel = createTaskGroup(categoryId, listOf(newData), backgroundColor)
                insertOrUpdateTaskGroup(taskGroupModel.getOrThrow())
            }
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
        backgroundColor: Int,
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
                backgroundColor = backgroundColor,
                totalPrice = taskGroupRepositoryImpl.getTotalPrice(currentTaskList)
            )
        return Result.success(taskGroup)
    }

    suspend fun deleteByCategory(categoryId: String) =
        withContext(Dispatchers.Default) {
            val message = taskGroupRepositoryImpl.deleteByCategory(categoryId)

            setMessage(message)
        }

    suspend fun deleteAll() =
        withContext(Dispatchers.Default) {
            val message = taskGroupRepositoryImpl.deleteAll()

            setMessage(message)
        }

    fun setMessage(result: Result<String>) {
        val message = result.getOrThrow()
        _resultMessage.value = Status(message, result.isSuccess)
    }

    suspend fun updateTotalPrice(categoryId: String, newTotal: Int) =
        withContext(Dispatchers.Default) {
            val message = taskGroupRepositoryImpl.updateCategoryTotal(categoryId, newTotal)
            setMessage(message)
        }
}
