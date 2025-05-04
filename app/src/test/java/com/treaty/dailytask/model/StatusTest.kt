package com.treaty.dailytask.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StatusTest {

    @Test
    fun verifyStatusModel() {
        val status = Status("Message", true)
        assertEquals(status.statusMessage, "Message")
        assertTrue(status.statusValue)
    }
}
