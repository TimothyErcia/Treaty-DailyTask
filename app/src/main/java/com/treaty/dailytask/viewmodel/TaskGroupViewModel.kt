package com.treaty.dailytask.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import com.treaty.dailytask.model.Task
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.log

@OptIn(ExperimentalCoroutinesApi::class)
class TaskGroupViewModel(private val taskGroupRepository: TaskGroupRepository) : ViewModel() {

    private val _taskGroupObject = MutableStateFlow<List<TaskGroupModel>>(emptyList())
    val taskGroup = _taskGroupObject.asStateFlow()

    private val _totalSum = MutableStateFlow<Int>(0)
    val totalSum = _totalSum.asStateFlow()

    init {
        viewModelScope.launch {
            val deferred = listOf(
                async { getAllTaskGroup() },
                async { getTotalSum() }
            )

            awaitAll(*deferred.toTypedArray())
        }
    }

    suspend fun createNewTaskGroup(taskGroupObject: TaskGroupObject) = withContext(Dispatchers.IO) {
        taskGroupRepository.insert(taskGroupObject)
    }

    private suspend fun getAllTaskGroup() {
        taskGroupRepository.getAllTaskGroup()
            .flowOn(Dispatchers.IO)
            .mapLatest {
                it.map { taskGroup ->
                    TaskGroupModel(
                        taskGroup.taskGroupUUID,
                        taskGroup.categoryID,
                        taskGroup.taskList,
                        getTotalPrice(taskGroup.taskList),
                        parseDate(getLastUpdate(taskGroup.taskList))
                    )
                }
            }
            .stateIn(viewModelScope)
            .collectLatest {
                _taskGroupObject.value = it
            }
    }

    private fun parseDate(date: String): String {
        val localDate = LocalDateTime.parse(date)
        return localDate.format(DateTimeFormatter.ofPattern("dd MMM YYYY HH:mm "))
    }
    private fun getTotalPrice(taskList: List<Task>): Int = taskList.sumOf { task -> task.price }
    private fun getLastUpdate(taskList: List<Task>) = taskList.lastOrNull()?.dateAdded ?: ""

    private suspend fun getTotalSum() {
        _taskGroupObject.collectLatest {data ->
            val sum = data.sumOf { it.totalPrice }
            _totalSum.value = sum
        }
    }
}