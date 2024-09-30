package com.example.task

import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Insert
    fun insertTask(task: Task): Long

    @Update
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)
}
