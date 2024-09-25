package com.treaty.dailytask.model.Task

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.UUID

open class TaskObject() : RealmObject {
    @PrimaryKey
    var taskId: ObjectId = ObjectId.invoke()
    var price: Int = 0
    var dateAdded: String = ""

    constructor(price: Int, dateAdded: String) : this() {
        this.price = price
        this.dateAdded = dateAdded
    }
}