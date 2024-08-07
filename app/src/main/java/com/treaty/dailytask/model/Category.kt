package com.treaty.dailytask.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Category : RealmObject {
    @PrimaryKey
    var categoryId: ObjectId = ObjectId.invoke()
    var category: String = ""
}