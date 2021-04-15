package com.example.todolist.const

import java.security.cert.CRLReason

object TasksContract {
    const val TABLE_NAME = "tasks_table"

    object Columns{
        const val ID = "id"
        const val NAME = "name"
        const val IMPORTANT = "important"
        const val COMPLETED = "completed"
        const val CREATED_AT = "created_at"
    }
}