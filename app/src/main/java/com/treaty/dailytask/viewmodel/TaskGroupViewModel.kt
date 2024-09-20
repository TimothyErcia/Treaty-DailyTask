package com.treaty.dailytask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepository
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class TaskGroupViewModel(private val taskGroupRepository: TaskGroupRepository) : ViewModel() {

    private val _taskGroup = MutableStateFlow<List<TaskGroupModel>>(emptyList())
    val taskGroup = _taskGroup.asStateFlow()

    init {
        viewModelScope.launch {
            getAllTaskGroup()
        }
    }

    suspend fun insertOrUpdateTaskGroup(taskGroupModel: TaskGroupModel) = withContext(Dispatchers.IO) {
        val list = realmListOf<TaskObject>()
        taskGroupModel.taskModelList.forEach {
            list.add(TaskObject(it.price, it.dateAdded))
        }
        taskGroupRepository.insert(TaskGroupObject(taskGroupModel.categoryID, list, taskGroupModel.backgroundColor))
    }

    private suspend fun getAllTaskGroup() = withContext(Dispatchers.IO) {
        taskGroupRepository.getAllTaskGroup()
            .mapLatest {
                it.map { taskGroup ->
                    val taskModelList = parseTaskGroupObject(taskGroup.taskModelList)
                    TaskGroupModel(
                        taskGroup.taskGroupUUID,
                        taskGroup.categoryID,
                        taskModelList,
                        getTotalPrice(taskModelList),
                        parseDate(getLastUpdate(taskModelList)),
                        taskGroup.backgroundColor
                    )
                }
            }
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
}