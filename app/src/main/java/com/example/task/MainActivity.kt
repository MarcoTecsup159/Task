package com.example.task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels { TaskViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskScreen(taskViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(taskViewModel: TaskViewModel = viewModel()) {
    val taskList by taskViewModel.tasks.collectAsState()  // Observar el estado de las tareas
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Acción para agregar una nueva tarea
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Add New Task Clicked")
                }
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            TaskListView(taskList, taskViewModel)
        }
    }
}

@Composable
fun TaskListView(taskList: List<Task>, taskViewModel: TaskViewModel) {
    if (taskList.isEmpty()) {
        //mostrar mensaje si no hay tareas
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay tareas disponibles. Agregar una nueva tarea.")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(taskList) { task ->
                TaskItem(task = task, onTaskCheckedChange = {
                    taskViewModel.toggleTaskCompletion(task)
                }, onDeleteTask = {
                    taskViewModel.deleteTask(task)
                }, onEditTask = {
                    taskViewModel.selectTask(task)
                })
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskCheckedChange: (Boolean) -> Unit,
    onDeleteTask: () -> Unit,
    onEditTask: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else null
                )
                if (!task.description.isNullOrEmpty()) {
                    Text(
                        text = task.description ?: "",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row {
                Checkbox(
                    checked = task.completed,
                    onCheckedChange = { onTaskCheckedChange(it) }
                )
                IconButton(onClick = onEditTask) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Task")
                }
                IconButton(onClick = onDeleteTask) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskScreen()
}