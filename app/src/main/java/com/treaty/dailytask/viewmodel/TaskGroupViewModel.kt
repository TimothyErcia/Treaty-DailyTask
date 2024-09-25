package com.treaty.dailytask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepository
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class TaskGroupViewModel(private val taskGroupRepository: TaskGroupRepository) : ViewModel() {

    private val _taskGroup = MutableStateFlow<List<TaskGroupModel>>(emptyList())
    val taskGroup = _taskGroup
        .onStart { getAllTaskGroup() }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    suspend fun insertOrUpdateTaskGroup(taskGroupModel: TaskGroupModel) = viewModelScope.launch(Dispatchers.IO) {
        // TODO validation
        val list = taskGroupModel.taskModelList.map {
            TaskObject(it.price, it.dateAdded)
        }.toRealmList()
        taskGroupRepository.insertOrUpdate(TaskGroupObject(taskGroupModel.categoryID, list, taskGroupModel.backgroundColor))
    }

    private suspend fun getAllTaskGroup() = viewModelScope.launch(Dispatchers.IO) {
        taskGroupRepository.getAllTaskGroup()
            .mapLatest { mapToTaskGroup(it) }
            .stateIn(viewModelScope)
            .collectLatest {
                _taskGroup.value = it
            }
    }

    private fun parseDate(date: String): String {
        val localDate = LocalDateTime.parse(date)
        return localDate.format(DateTimeFormatter.ofPattern("dd MMM YYYY HH:mm "))
    }
    private fun getTotalPrice(taskModelList: List<TaskModel>): Int = taskModelList.sumOf { task -> task.price }
    private fun getLastUpdate(taskModelList: List<TaskModel>) = taskModelList.lastOrNull()?.dateAdded ?: ""

    private fun parseTaskGroupObject(taskModelList: List<TaskObject>): List<TaskModel> {
        return taskModelList.map { data ->
            TaskModel(data.taskId.toString(), data.price, data.dateAdded)
        }
    }

    fun getTotalSum(data: List<TaskGroupModel>): Int {
        if(data.isEmpty()) {
            return 0
        }

        return data.sumOf { it.totalPrice }
    }

     suspend fun getAllTaskByCategory(categoryId: String): Flow<List<TaskGroupModel>> {
         return taskGroupRepository.getAllTaskGroupByCategory(categoryId)
             .mapLatest { mapToTaskGroup(it) }
             .flowOn(Dispatchers.IO)
     }

    fun createNewTask(price: Int): TaskModel {
        // TODO validation
        return TaskModel(price = price, dateAdded = LocalDateTime.now().toString())
    }

    fun createTaskGroup(category: String, currentTaskList: List<TaskModel>, backgroundColor: Int): TaskGroupModel {
        // TODO validation
        return TaskGroupModel(categoryID = category, taskModelList = currentTaskList, backgroundColor = backgroundColor)
    }

    private fun mapToTaskGroup(data: List<TaskGroupObject>) : List<TaskGroupModel> {
        //TODO map return extension
        return data.map { taskGroup ->
            val taskModelList = parseTaskGroupObject(taskGroup.taskModelList)
            TaskGroupModel(
                taskGroup.categoryID,
                taskModelList,
                getTotalPrice(taskModelList),
                parseDate(getLastUpdate(taskModelList)),
                taskGroup.backgroundColor
            )
        }
    }
}