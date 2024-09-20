package com.treaty.dailytask.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.repository.task.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private var _taskModel = MutableStateFlow<List<TaskModel>>(emptyList())
    var task = _taskModel.asStateFlow()

    init {
        viewModelScope.launch {
            getAllTask()
        }
    }

    suspend fun insertTask(taskModel: TaskModel) {
        if(validateModel(taskModel)) {
            return
        }
        taskRepository.insert(TaskObject(taskModel.price, taskModel.dateAdded))
    }

    private suspend fun getAllTask() = withContext(Dispatchers.IO) {
        taskRepository.getAllTask()
            .mapLatest { data ->
                data.map { TaskModel(it.taskId.toString(), it.price, it.dateAdded) }
            }
            .collectLatest {
                Log.d("TAG", "getAllTask: ${it}")
                _taskModel.value = it
            }
    }

    private fun validateModel(taskModel: TaskModel): Boolean {
        return taskModel.price <= 0 && taskModel.dateAdded.isBlank()
    }
}