package com.treaty.dailytask.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Task : RealmObject {
    @PrimaryKey
    var taskId: ObjectId = ObjectId.invoke()
    var categoryId: Category? = null
    var statusId: Status? = null
    var price = 0
    var description: String = ""
}