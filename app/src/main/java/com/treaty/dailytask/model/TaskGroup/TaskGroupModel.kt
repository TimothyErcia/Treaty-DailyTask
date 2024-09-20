package com.treaty.dailytask.model.TaskGroup

import com.treaty.dailytask.model.Task.TaskModel

data class TaskGroupModel(
    var taskGroupUUID: String = "",
    var categoryID: String,
    var taskModelList: List<TaskModel>,
    var totalPrice: Int = 0,
    var lastUpdate: String = "",
    var backgroundColor: Int = 0
)
