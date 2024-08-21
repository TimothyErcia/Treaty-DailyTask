package com.treaty.dailytask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treaty.dailytask.model.TaskGroup
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class TaskGroupViewModel(private val taskGroupRepository: TaskGroupRepository) : ViewModel() {

    private var _taskGroup = MutableStateFlow<List<TaskGroup>>(emptyList())
    var taskGroup = _taskGroup.asStateFlow()

    init {
        viewModelScope.launch {
            getAllTaskGroup()
        }
    }

    suspend fun createNewTaskGroup(taskGroup: TaskGroup) {
        taskGroupRepository.insert(taskGroup)
    }

    private suspend fun getAllTaskGroup() {
        taskGroupRepository.getAllTaskGroup()
            .flowOn(Dispatchers.IO)
            .collect {
                _taskGroup.value = it
        }
    }
}