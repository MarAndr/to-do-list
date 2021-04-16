package com.example.todolist.data

import androidx.room.*
import com.example.todolist.const.TasksContract
import com.example.todolist.ui.tasks.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao{

    fun getTasks(searchQuery: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
            when(sortOrder){
                SortOrder.BY_DATE -> getTasksSortedByDateCreated(searchQuery, hideCompleted)
                SortOrder.BY_NAME -> getTasksSortedByName(searchQuery, hideCompleted)
            }

    @Query("SELECT * FROM ${TasksContract.TABLE_NAME} WHERE (${TasksContract.Columns.COMPLETED} != :hideCompleted OR ${TasksContract.Columns.COMPLETED} = 0) AND ${TasksContract.Columns.NAME} LIKE '%' || :searchQuery || '%' ORDER BY ${TasksContract.Columns.IMPORTANT} DESC, ${TasksContract.Columns.NAME}")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM ${TasksContract.TABLE_NAME} WHERE (${TasksContract.Columns.COMPLETED} != :hideCompleted OR ${TasksContract.Columns.COMPLETED} = 0) AND ${TasksContract.Columns.NAME} LIKE '%' || :searchQuery || '%' ORDER BY ${TasksContract.Columns.IMPORTANT} DESC, ${TasksContract.Columns.CREATED_AT}")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}