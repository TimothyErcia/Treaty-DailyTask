package com.treaty.dailytask.model.TaskGroup

import io.realm.kotlin.ext.isValid
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertTrue

class TaskGroupTest {

    @Test
    fun verifyTaskGroupObject() {
        val taskGroup = TaskGroupObject()
        assertTrue(taskGroup.isValid())
        assertTrue(taskGroup.taskModelList.isEmpty())
        assertEquals(taskGroup.backgroundColor, 0)
        assertEquals(taskGroup.totalPrice, 0)
    }
}
