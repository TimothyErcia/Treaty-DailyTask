package com.treaty.dailytask.model.TaskGroup

import com.treaty.dailytask.model.Task.TaskObject
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey

open class TaskGroupObject() : RealmObject {
    @PrimaryKey
    var categoryID: String = ""
    var taskModelList: RealmList<TaskObject> = realmListOf()
    var backgroundColor: Int = 0

    constructor(
        categoryID: String,
        taskModelList: RealmList<TaskObject>,
        backgroundColor: Int
    ): this() {
        this.categoryID = categoryID
        this.taskModelList = taskModelList
        this.backgroundColor = backgroundColor
    }
}