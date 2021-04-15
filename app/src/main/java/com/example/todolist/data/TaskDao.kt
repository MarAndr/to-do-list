package com.example.todolist.data

import androidx.room.*
import com.example.todolist.const.TasksContract
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao{
    @Query("SELECT * FROM ${TasksContract.TABLE_NAME}")
    fun getTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}