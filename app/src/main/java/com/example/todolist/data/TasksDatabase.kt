package com.example.todolist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolist.data.TasksDatabase.Companion.DB_VERSION
import com.example.todolist.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = DB_VERSION)
abstract class TasksDatabase: RoomDatabase() {
    companion object{
        const val DB_NAME = "task_database"
        const val DB_VERSION = 1
    }

    abstract fun tasksDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TasksDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dao = database.get().tasksDao()

            applicationScope.launch {
                dao.insertTask(Task(name = "Wash the dishes"))
                dao.insertTask(Task(name = "Do the laundry"))
                dao.insertTask(Task(name = "Buy groceries", important = true))
                dao.insertTask(Task(name = "Prepare food", completed = true))
                dao.insertTask(Task(name = "Visit grandma", completed = true))
                dao.insertTask(Task(name = "Wash the dishes"))
                dao.insertTask(Task(name = "Wash the dishes"))
            }
        }
    }
}