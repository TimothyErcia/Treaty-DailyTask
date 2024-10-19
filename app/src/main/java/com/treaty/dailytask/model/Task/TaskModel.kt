package com.treaty.dailytask.model.Task

import java.util.UUID

data class TaskModel(
    var taskUUID: String = "",
    var price: Int = 0,
    var dateAdded: String = ""
)