package com.treaty.dailytask.repository.reminder

import com.treaty.dailytask.model.Reminder
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.query.RealmResults

class ReminderRepositoryImpl(private val realm: Realm): ReminderRepository {
    override suspend fun updateReminderTrigger(reminder: Reminder) {
        realm.write {
            copyToRealm(
                reminder,
                UpdatePolicy.ALL
            )
        }
    }

    override suspend fun getReminderStatus(): RealmResults<Reminder> {
        return realm.query(Reminder::class).find()
    }

}
