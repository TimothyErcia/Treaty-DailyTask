package com.treaty.dailytask.viewmodel

import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepository
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TaskGroupViewModelUnitTest {

    private lateinit var taskGroupViewModel: TaskGroupViewModel
    private var mockTaskGroupModelList = listOf(
        TaskGroupModel("Food", emptyList(), 100, LocalDateTime.now().toString(), 0),
        TaskGroupModel("Food", emptyList(), 100, LocalDateTime.now().toString(), 0),
        TaskGroupModel("Food", emptyList(), 100, LocalDateTime.now().toString(), 0)
    )
    private val mockTaskGroupModel = TaskGroupModel("Food", emptyList(), 100, LocalDateTime.now().toString(), 0)
    private val mockTaskModel = TaskModel(UUID.randomUUID().toString(), 100, LocalDateTime.now().toString())
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeRepository: TaskGroupRepository
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeRepositoryImpl()
        taskGroupViewModel = TaskGroupViewModel(fakeRepository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `taskGroupViewModel success`() = runTest {
        backgroundScope.launch(testDispatcher) {
            taskGroupViewModel.taskGroup.collect()
        }
        val task = taskGroupViewModel.taskGroup.value
        assertTrue(task.isEmpty())

        backgroundScope.launch(testDispatcher) {
            val task = taskGroupViewModel.insertOrUpdateTaskGroup(mockTaskGroupModel)
            assertEquals(task, "success")

            taskGroupViewModel.taskGroup.collect()
            val task2 = taskGroupViewModel.taskGroup.value
            assertTrue(task2.isNotEmpty())
            assertEquals(task2[0].categoryID, "Food")
        }
    }

    @Test
    fun `taskGroupViewModel empty list`() = runTest(testDispatcher) {
        fakeRepository = FakeRepositoryImpl2()
        taskGroupViewModel = TaskGroupViewModel(fakeRepository)
        backgroundScope.launch {
            taskGroupViewModel.taskGroup.collect {
                assertTrue(it.isEmpty())
            }
        }
    }

    @Test
    fun `taskGroupViewModel filtered success`() = runTest {
        backgroundScope.launch(testDispatcher) {
            taskGroupViewModel.getAllTaskByCategory("Food").collect {
                assertTrue(it.isNotEmpty())
                assertEquals(it.first().totalPrice, 200)
            }
        }
    }

    @Test
    fun `taskGroupViewModel filtered empty list`() = runTest {
        val fakeRepository = FakeRepositoryImpl2()
        taskGroupViewModel = TaskGroupViewModel(fakeRepository)
        backgroundScope.launch(testDispatcher) {
            taskGroupViewModel.getAllTaskByCategory("Home").collect {
                assertTrue(it.isEmpty())
            }
        }
    }

    @Test
    fun `total sum with computed values`() {
        val total = taskGroupViewModel.getTotalSum(mockTaskGroupModelList)
        assertEquals(total, 300)
    }

    @Test
    fun `total sum with 0`() {
        val total = taskGroupViewModel.getTotalSum(emptyList())
        assertEquals(total, 0)
    }

    @Test
    fun `create new task success result`() {
        val result = taskGroupViewModel.createNewTask("100")
        result.onSuccess {
            assertEquals(it.price, 100)
            assertNotNull(it.dateAdded)
            assertNotNull(it.taskUUID)
        }
        assertTrue(result.isSuccess)
    }

    @Test
    fun `create new task failed null result`() {
        val result = taskGroupViewModel.createNewTask("")
        result.onFailure {
            assertEquals(it.message, "Error message")
        }
        assertTrue(result.isFailure)
    }

    @Test
    fun `create new task failed 0 result`() {
        val result = taskGroupViewModel.createNewTask("0")
        result.onFailure {
            assertEquals(it.message, "Error message")
        }
        assertTrue(result.isFailure)
    }

    @Test
    fun `create taskgroup success`() {
        val task = taskGroupViewModel.createTaskGroup("Food", listOf(mockTaskModel, mockTaskModel), 0)
        task.onSuccess {
            assertEquals(it.categoryID, "Food")
            assertEquals(it.taskModelList.count(), 2)
        }
        assertTrue(task.isSuccess)
    }

    @Test
    fun `create taskgroup fail empty list`() {
        val task = taskGroupViewModel.createTaskGroup("Food", emptyList(), 0)
        task.onFailure {
            assertEquals(it.message, "Error Message")
        }
        assertTrue(task.isFailure)
    }

    @Test
    fun `create taskgroup fail category empty`() {
        val task = taskGroupViewModel.createTaskGroup("", listOf(mockTaskModel, mockTaskModel), 0)
        task.onFailure {
            assertEquals(it.message, "Error Message")
        }
        assertTrue(task.isFailure)
    }

    inner class FakeRepositoryImpl2 : TaskGroupRepository {
        override suspend fun insertOrUpdate(taskGroupObject: TaskGroupObject): Result<String> {
            return Result.failure(Throwable("failed"))
        }

        override suspend fun getAllTaskGroup(): Flow<List<TaskGroupObject>> {
            return emptyFlow()
        }

        override suspend fun getAllTaskGroupByCategory(categoryId: String): Flow<List<TaskGroupObject>> {
            return emptyFlow()
        }

    }

    inner class FakeRepositoryImpl : TaskGroupRepository {
        private val flow = MutableStateFlow<List<TaskGroupObject>>(emptyList())
        private var listTaskGroup = listOf(
            TaskGroupObject("Food", realmListOf(TaskObject(100, LocalDateTime.now().plusHours(1).toString())), 0),
            TaskGroupObject("Food", realmListOf(TaskObject(100, LocalDateTime.now().plusHours(2).toString())), 0),
        )
        override suspend fun insertOrUpdate(taskGroupObject: TaskGroupObject): Result<String> {
            listTaskGroup = listTaskGroup.plus(taskGroupObject)
            flow.emit(listTaskGroup)
            return Result.success("success")
        }

        override suspend fun getAllTaskGroup(): Flow<List<TaskGroupObject>> {
            return flow
        }

        override suspend fun getAllTaskGroupByCategory(categoryId: String): Flow<List<TaskGroupObject>> {
            return flow
        }
    }
}
