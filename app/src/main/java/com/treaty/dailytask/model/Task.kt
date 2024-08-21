package com.treaty.dailytask.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.Date

open class Task() : RealmObject {
    @PrimaryKey
    var taskId: String = ""
    var price: Int = 0
    var dateAdded: Date? = null

    constructor(price: Int) : this() {
        this.taskId = ObjectId.invoke().toString()
        this.price = price
        this.dateAdded = Date()
    }
}