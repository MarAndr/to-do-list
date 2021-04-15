package com.example.todolist.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todolist.const.TasksContract
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = TasksContract.TABLE_NAME)
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = TasksContract.Columns.ID)
    val id: Int = 0,
    @ColumnInfo(name = TasksContract.Columns.NAME)
    val name: String,
    @ColumnInfo(name = TasksContract.Columns.IMPORTANT)
    val important: Boolean = false,
    @ColumnInfo(name = TasksContract.Columns.COMPLETED)
    val completed: Boolean = false,
    @ColumnInfo(name = TasksContract.Columns.CREATED_AT)
    val createdAt: Long = System.currentTimeMillis()

) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdAt)
}