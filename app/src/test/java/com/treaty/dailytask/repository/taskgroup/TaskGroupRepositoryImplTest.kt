package com.treaty.dailytask.repository.taskgroup

import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.repository.FakeRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TaskGroupRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val realmConfiguration = RealmConfiguration.Builder(schema = setOf(TaskGroupObject::class, TaskObject::class))
        .inMemory()
        .name("test-realm")
        .build()

    private val realm = Realm.open(realmConfiguration)
    private val taskGroupDAO = TaskGroupDAO(realm)
    private val taskGroupRepositoryImpl = TaskGroupRepositoryImpl(taskGroupDAO)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insertOrUpdate method call return Result success `() = runTest(testDispatcher) {
        val mockTaskGroupModel = TaskGroupModel("Food", listOf(TaskModel("asd123", 100, "2020")), 100, "2020", 0)
        val res = taskGroupRepositoryImpl.insertOrUpdate(mockTaskGroupModel)
        assertTrue(res.isSuccess)
    }

    @Test
    fun `insertOrUpdate method call return Result fail`() = runTest(testDispatcher) {
        val mockTaskGroupModel = TaskGroupModel("Food", emptyList())
        val res = taskGroupRepositoryImpl.insertOrUpdate(mockTaskGroupModel)
        assertTrue(res.isFailure)
    }

    @Test
    fun `getAllTaskGroup returns Flow TaskGroupModel`() = runTest(testDispatcher) {
        val res = taskGroupRepositoryImpl.getAllTaskGroup()
        assertTrue(res is Flow<List<TaskGroupModel>>)
    }

    @Test
    fun `getAllTaskGroup map TaskGroupModel to TaskGroupObject`() = runTest(testDispatcher) {
        val taskGroupRepositoryImpl = TaskGroupRepositoryImpl(FakeRepository.FakeRepositoryDAO())
        val res = taskGroupRepositoryImpl.getAllTaskGroup()
        backgroundScope.launch {
            res.collect {
                assertTrue(it.isNotEmpty())
            }
        }
    }

    @Test
    fun `getAllTaskGroup map TaskGroupObject to TaskGroupModel`() = runTest(testDispatcher) {
        backgroundScope.launch {
            taskGroupDAO.insertOrUpdate(TaskGroupObject("Home", realmListOf(TaskObject(100, "2020")), 0))
            val res = taskGroupDAO.getAllTaskGroup()
            res.collect {
                assertTrue(it.isNotEmpty())
                assertEquals(it[0].categoryID, "Home")
            }
        }
    }

    @Test
    fun `getAllTaskGroupByCategory without parameter`() = runTest(testDispatcher) {
        val res = taskGroupRepositoryImpl.updateByCategory("", TaskModel())
        assertTrue(res.isFailure)
    }

    @Test
    fun `deleteByCategory returns success`() = runTest(testDispatcher) {
        val res = taskGroupRepositoryImpl.deleteByCategory("Food")
        assertTrue(res.isSuccess)
    }

    @Test
    fun `deleteByCategory returns failed`() = runTest(testDispatcher) {
        val res = taskGroupRepositoryImpl.deleteByCategory("")
        assertTrue(res.isFailure)
    }
}
