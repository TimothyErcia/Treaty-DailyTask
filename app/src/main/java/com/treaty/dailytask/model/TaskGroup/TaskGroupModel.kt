package com.treaty.dailytask.model.TaskGroup

import com.treaty.dailytask.model.Task

data class TaskGroupModel(
    var taskGroupUUID: String,
    var categoryID: String,
    var taskList: List<Task>,
    var totalPrice: Int,
    var lastUpdate: String,
)
