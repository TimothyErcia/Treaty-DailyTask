package com.treaty.dailytask.model.TaskGroup

import com.treaty.dailytask.model.Task
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey

open class TaskGroupObject() : RealmObject {
    @PrimaryKey
    var taskGroupUUID: String = RealmUUID.random().toString()
    var categoryID: String = ""
    var taskList: RealmList<Task> = realmListOf()

    constructor(
        categoryID: String,
        taskList: RealmList<Task>
    ): this() {
        this.categoryID = categoryID
        this.taskList = taskList
    }
}