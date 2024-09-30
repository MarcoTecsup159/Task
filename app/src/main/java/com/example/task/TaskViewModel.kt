package com.example.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    private val _tasks: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    //estado para manejar una sola tarea seleccionada
    private val _selectedTask: MutableStateFlow<Task?> = MutableStateFlow(null)
    val selectedTask: StateFlow<Task?> = _selectedTask

    //cargar todas las tareas desde la bd
    fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val taskList = taskDao.getAllTasks()
            _tasks.value = taskList
        }
    }

    // insertar nueva tarea
    fun addTask(title: String, description: String?, dueDate: Date?, priority: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val newTask = Task(
                title = title,
                description = description,
                dueDate = dueDate,
                priority = priority,
                completed = false
            )
            taskDao.insertTask(newTask)
            loadTasks() 
        }
    }

    // actualizar una tarea existente
    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.updateTask(task)
            loadTasks()
        }
    }

    //eliminar una tarea
    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteTask(task)
            loadTasks()
        }
    }

    // seleccionar una tarea para editar o ver detalles
    fun selectTask(task: Task) {
        _selectedTask.value = task
    }

    // deseleccionar una tarea
    fun clearSelectedTask() {
        _selectedTask.value = null
    }

    //marcar una tarea como completada o incompleta
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTask = task.copy(completed = !task.completed)
            taskDao.updateTask(updatedTask)
            loadTasks()
        }
    }
}
