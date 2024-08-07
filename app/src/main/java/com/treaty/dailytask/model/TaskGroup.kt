package com.treaty.dailytask.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey

class TaskGroup : RealmObject {
    @PrimaryKey
    var taskGroupUUID: RealmUUID = RealmUUID.random()

    var cutoffDate: RealmInstant = RealmInstant.now()
    var totalPrice = 0
    var taskId: RealmList<Task> = realmListOf()
}