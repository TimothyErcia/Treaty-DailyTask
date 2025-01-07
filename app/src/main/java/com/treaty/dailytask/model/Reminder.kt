package com.treaty.dailytask.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneOffset

class Reminder() : RealmObject {
    @PrimaryKey
    var reminderId: String = "Reminder Notification"
    var dateOfTrigger: String = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
    var isCompleted: Boolean = false

    constructor(isCompleted: Boolean): this() {
        this.isCompleted = isCompleted
    }
}