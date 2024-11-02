package com.treaty.dailytask.model.Task

import io.realm.kotlin.ext.isValid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskTest {

    @Test
    fun verifyTaskObject() {
        val task = TaskObject()
        assertTrue(task.isValid())
        assertEquals(task.price, 0)
        assertEquals(task.dateAdded, "")
    }
}
