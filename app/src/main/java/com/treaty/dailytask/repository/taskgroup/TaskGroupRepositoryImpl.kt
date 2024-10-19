package com.treaty.dailytask.repository.taskgroup

import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskGroupRepositoryImpl(private val realm: Realm) : TaskGroupRepository {
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

    override suspend fun getAllTaskGroupByCategory(categoryId: String): Flow<List<TaskGroupObject>> {
        val realmQuery = realm.query(TaskGroupObject::class, "categoryID == $0", categoryId).find()
        val realmFlow = realmQuery.asFlow()

        return realmFlow.map { it.list }
    }

    override suspend fun deleteByCategory(categoryId: String): Result<String> {
        realm.write {
            val category = query(TaskGroupObject::class, "categoryID == $0", categoryId).find()
            delete(category)
        }
        return Result.success("Category Removed")
    }

}