package com.treaty.dailytask.viewmodel

import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.repository.FakeRepository
import com.treaty.dailytask.repository.taskgroup.TaskGroupDAO
import com.treaty.dailytask.repository.taskgroup.TaskGroupRepositoryImpl
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
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
        TaskGroupModel("Food", emptyList(), 100, LocalDateTime.now().toString(), -3937903),
        TaskGroupModel("Food", emptyList(), 100, LocalDateTime.now().plusMinutes(1).toString(), -3937903),
        TaskGroupModel("Food", emptyList(), 100, LocalDateTime.now().plusMinutes(1).toString(), -3937903)
    )
    private val mockTaskModel = TaskModel(UUID.randomUUID().toString(), 100, LocalDateTime.now().toString())
    private val mockTaskGroupModel = TaskGroupModel("Food", emptyList(), 100, LocalDateTime.now().toString(), -3937903)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var taskGroupRepositoryImpl: TaskGroupRepositoryImpl
    private val realmConfiguration = RealmConfiguration.Builder(schema = setOf(TaskGroupObject::class, TaskObject::class))
        .inMemory()
        .name("test-realm")
        .build()
    private val realm = Realm.open(realmConfiguration)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskGroupRepositoryImpl = TaskGroupRepositoryImpl(TaskGroupDAO(realm))
        taskGroupViewModel = TaskGroupViewModel(taskGroupRepositoryImpl)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCategoryAndInsert categoryRes value is empty`() = runTest(testDispatcher) {
        taskGroupViewModel.getCategoryAndInsert("Condo", mockTaskModel, 0)
        var task = taskGroupViewModel.taskGroup.value
        assertTrue(task.isEmpty())
        backgroundScope.launch {
            task = taskGroupViewModel.taskGroup.value
            assertTrue(task.isNotEmpty())
            assertEquals(task[0].categoryID, "Condo")
        }
    }

    @Test
    fun `taskGroupViewModel empty list`() = runTest(testDispatcher) {
        backgroundScope.launch {
            taskGroupRepositoryImpl.insertOrUpdate(TaskGroupModel("", emptyList(), 0, "", 0))
            taskGroupViewModel = TaskGroupViewModel(taskGroupRepositoryImpl)
            taskGroupViewModel.taskGroup.collect {
                assertTrue(it.isEmpty())
            }
        }
    }

    @Test
    fun `taskGroupViewModel filtered and insert with filled list success`() = runTest(testDispatcher) {
        val mockTaskGroupModel = TaskGroupModel("Food", listOf(mockTaskModel), 100, LocalDateTime.now().toString(), -3937903)
        taskGroupRepositoryImpl.insertOrUpdate(mockTaskGroupModel)
        taskGroupViewModel = TaskGroupViewModel(taskGroupRepositoryImpl)
        taskGroupViewModel.getCategoryAndInsert("Food", mockTaskModel, 0)
        backgroundScope.launch {
            assertEquals(taskGroupViewModel.resultMessage.value, "Successfully Added")
        }
    }

    @Test
    fun `taskGroupViewModel filtered and insert with empty list success`() = runTest(testDispatcher) {
        val mockTaskGroupModel = TaskGroupModel("Food", listOf(mockTaskModel), 100, LocalDateTime.now().toString(), -3937903)
        taskGroupRepositoryImpl.insertOrUpdate(mockTaskGroupModel)
        taskGroupViewModel = TaskGroupViewModel(taskGroupRepositoryImpl)
        taskGroupViewModel.getCategoryAndInsert("Food", mockTaskModel, 0)
        backgroundScope.launch {
            assertEquals(taskGroupViewModel.resultMessage.value, "Successfully Added")
        }
    }


    @Test
    fun `getAllTaskGroup taskgroupObject to taskgroupModel`() = runTest(testDispatcher) {
        taskGroupRepositoryImpl.insertOrUpdate(mockTaskGroupModel)
        taskGroupViewModel = TaskGroupViewModel(taskGroupRepositoryImpl)
        backgroundScope.launch {
            taskGroupViewModel.taskGroup.collect()
            val task2 = taskGroupViewModel.taskGroup.value
            assertTrue(task2.isNotEmpty())
            assertEquals(task2[0].categoryID, "Food")
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

    @Test
    fun `delete data by category success`() = runTest(testDispatcher) {
        backgroundScope.launch {
            taskGroupViewModel.taskGroup.collect()
            val task2 = taskGroupViewModel.taskGroup.value
            assertTrue(task2.isNotEmpty())
            assertEquals(task2[0].categoryID, "Food")
        }
        taskGroupViewModel.deleteByCategory("Food")
        taskGroupRepositoryImpl.deleteByCategory("Food")
        assertTrue(taskGroupViewModel.taskGroup.value.isEmpty())
        assertEquals(taskGroupViewModel.resultMessage.value, "Category Removed")
    }

    @Test
    fun `deleteAll method call and return Result success`() = runTest(testDispatcher) {
        taskGroupRepositoryImpl = TaskGroupRepositoryImpl(FakeRepository.FakeRepositoryDAO())
        taskGroupViewModel = TaskGroupViewModel(taskGroupRepositoryImpl)
        backgroundScope.launch {
            taskGroupViewModel.taskGroup.collect()
            val task2 = taskGroupViewModel.taskGroup.value
            assertTrue(task2.isNotEmpty())
            assertEquals(task2[0].categoryID, "Food")
        }
        taskGroupViewModel.deleteAll()
        taskGroupRepositoryImpl.deleteAll()
        assertTrue(taskGroupViewModel.taskGroup.value.isEmpty())
        assertEquals(taskGroupViewModel.resultMessage.value, "Successfully removed All")
    }
}
