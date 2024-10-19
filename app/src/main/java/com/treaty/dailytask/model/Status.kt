package com.treaty.dailytask.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Status : RealmObject {
    @PrimaryKey
    var statusId: ObjectId = ObjectId.invoke()
    var status: String = ""
}