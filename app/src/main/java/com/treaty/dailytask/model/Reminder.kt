package com.treaty.dailytask.model

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Reminder() : RealmObject {
    @PrimaryKey
    var reminderId: String = "Reminder Notification"
    var dateOfTrigger: RealmInstant = RealmInstant.now()
    var isCompleted: Boolean = false

    constructor(isCompleted: Boolean): this() {
        this.isCompleted = isCompleted
    }
}