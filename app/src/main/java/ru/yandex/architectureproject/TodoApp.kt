package ru.yandex.architectureproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ru.yandex.architectureproject.model.TaskAction

@Composable
fun TodoApp(viewModel: TaskViewModel) {
    val state by viewModel.state.collectAsState()
    var newTaskText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = newTaskText,
                onValueChange = { newTaskText = it },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (newTaskText.isNotBlank()) {
                    viewModel.reduce(TaskAction.AddTask(newTaskText))
                    newTaskText = ""
                }
            }) {
                Text("Добавить")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(state.tasks) { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = task.text,
                        style = if (task.isDone)
                            MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = TextDecoration.LineThrough
                            )
                        else MaterialTheme.typography.bodyMedium
                    )

                    Row {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = {
                                viewModel.reduce(
                                    TaskAction.UpdateTaskStatus(task.id, it)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            viewModel.reduce(TaskAction.DeleteTask(task.id))
                        }) {
                            Text("Удалить")
                        }
                    }
                }
            }
        }
    }
}