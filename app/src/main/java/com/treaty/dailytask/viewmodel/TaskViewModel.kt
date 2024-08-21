package com.treaty.dailytask.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treaty.dailytask.model.Task
import com.treaty.dailytask.repository.task.TaskRepository
import io.realm.kotlin.ext.isValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private var _task = MutableStateFlow<List<Task>>(emptyList())
    var task = _task.asStateFlow()

    init {
        viewModelScope.launch {
            getAllTask()
        }
    }

    suspend fun insertTask(task: Task) {
        if(!task.isValid()) {
            Log.e("INSERT", "insertTask: Error")
            return
        }
        taskRepository.insert(task)
    }

    private suspend fun getAllTask() {
        taskRepository.getAllTask()
            .flowOn(Dispatchers.IO)
            .collect {
                _task.value = it
            }
    }

}