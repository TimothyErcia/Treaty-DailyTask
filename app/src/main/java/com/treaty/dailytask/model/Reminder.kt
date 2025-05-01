package com.treaty.dailytask.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneOffset

class Reminder() : RealmObject {
    @PrimaryKey
    var reminderId: String = "Reminder Notification"
    var dateOfTrigger: Long = 0L
    var isToggled: Boolean = false

    constructor(isToggled: Boolean, dateOfTrigger: Long): this() {
        this.isToggled = isToggled
        this.dateOfTrigger = dateOfTrigger
    }
}