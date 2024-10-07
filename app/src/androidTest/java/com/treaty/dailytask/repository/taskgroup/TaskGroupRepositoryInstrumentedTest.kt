package com.treaty.dailytask.repository.taskgroup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class TaskGroupRepositoryInstrumentedTest {
    private lateinit var realm: Realm
    private lateinit var taskGroupRepositoryImpl: TaskGroupRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val realmTestConfiguration = RealmConfiguration.Builder(
            schema = setOf(TaskGroupObject::class, TaskObject::class))
            .inMemory()
            .name("test-realm").build()
        realm = Realm.open(realmTestConfiguration)

        taskGroupRepositoryImpl = TaskGroupRepositoryImpl(realm)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        realm.close()
    }

    @Test
    fun insert_call_success() = runTest(testDispatcher) {
        val taskGroupObject = TaskGroupObject("Food", realmListOf(TaskObject(100, LocalDateTime.now().toString())), 0)
        backgroundScope.launch {
            val task = taskGroupRepositoryImpl.insertOrUpdate(taskGroupObject)
            Assert.assertTrue(task.isSuccess)

            val data = taskGroupRepositoryImpl.getAllTaskGroup()
            data.collect {
                Assert.assertTrue(it.isNotEmpty())
                Assert.assertEquals(it[0].categoryID, "Food")
                Assert.assertEquals(it[0].taskModelList[0].price, 100)
            }
        }
    }
}
