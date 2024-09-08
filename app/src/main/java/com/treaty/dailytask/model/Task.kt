package com.treaty.dailytask.model

import io.realm.kotlin.types.RealmObject
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime

open class Task() : RealmObject {
    var taskId: ObjectId = ObjectId.invoke()
    var price: Int = 0
    var dateAdded: String = ""

    constructor(price: Int) : this() {
        this.price = price
        this.dateAdded = LocalDateTime.now().toString()
    }
}