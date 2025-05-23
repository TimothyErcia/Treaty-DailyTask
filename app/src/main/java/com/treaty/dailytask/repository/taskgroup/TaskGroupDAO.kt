package com.treaty.dailytask.repository.taskgroup

import com.treaty.dailytask.model.Task.TaskObject
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.query.RealmQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskGroupDAO (private val realm: Realm) : TaskGroupRepository {
    override suspend fun insertOrUpdate(taskGroupObject: TaskGroupObject): Result<String> {
        realm.write {
            copyToRealm(
                taskGroupObject,
                UpdatePolicy.ALL
            )
        }
        return Result.success("Successfully Added")
    }

    override suspend fun getAllTaskGroup(): Flow<List<TaskGroupObject>> {
        val realmQuery = realm.query(TaskGroupObject::class).find()
        val realmFlow = realmQuery.asFlow()

        return realmFlow.map { it.list }
    }

    override suspend fun updateByCategory(categoryId: String, newData: TaskObject): Result<String> {
        val realmQuery: RealmQuery<TaskGroupObject> =
            realm.query(TaskGroupObject::class, "categoryID == $0", categoryId)
        val realmResult: TaskGroupObject? = realmQuery.first().find()

        if(realmResult != null) {
            realmResult.also { taskGroupObjects ->
                realm.writeBlocking {
                    findLatest(taskGroupObjects)?.taskModelList?.add(newData)
                    findLatest(taskGroupObjects)?.totalPrice =
                        newData.price + taskGroupObjects.totalPrice
                }
            }
            return Result.success("Successfully Added")
        }
        return Result.failure(Throwable("Error Message"))
    }

    override suspend fun deleteByCategory(categoryId: String): Result<String> {
        realm.write {
            val category = query(TaskGroupObject::class, "categoryID == $0", categoryId).find()
            delete(category)
        }
        return Result.success("Category Removed")
    }

    override suspend fun deleteAllTaskGroup(): Result<String> {
        realm.write {
            deleteAll()
        }
        return Result.success("Successfully removed All")
    }

    override suspend fun updateTotal(
        categoryId: String,
        newTotal: Int
    ): Result<String> {
        val realmQuery: RealmQuery<TaskGroupObject> =
            realm.query(TaskGroupObject::class, "categoryID == $0", categoryId)
        val realmResult: TaskGroupObject? = realmQuery.first().find()

        if (realmResult != null) {
            realmResult.also { taskGroupObjects ->
                realm.writeBlocking {
                    findLatest(taskGroupObjects)?.totalPrice = newTotal
                }
            }
            return Result.success("Successfully updated total")
        }
        return Result.failure(Throwable("Error Message"))
    }
}
