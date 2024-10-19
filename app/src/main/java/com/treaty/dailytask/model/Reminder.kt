package com.treaty.dailytask.model

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.Date

class Reminder : RealmObject {
    @PrimaryKey
    var reminderId: ObjectId = ObjectId.invoke()

    @Ignore
    var statusId: Status? = null

    var dateOfTrigger: RealmInstant = RealmInstant.now()
    var description: String = ""
}