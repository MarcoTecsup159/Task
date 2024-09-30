package com.example.task

import android.app.Application
import androidx.room.Room

class TaskApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "task_database"
        ).build()
    }
}