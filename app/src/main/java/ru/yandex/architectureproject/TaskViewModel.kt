package ru.yandex.architectureproject

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.yandex.architectureproject.model.Task
import ru.yandex.architectureproject.model.TaskAction
import ru.yandex.architectureproject.model.TaskState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository = InMemoryTaskRepository(),
    private val completeTaskUseCase: CompleteTaskUseCase =
        CompleteTaskUseCase(repository)
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state

    private val autoDeleteJobs = mutableMapOf<Int, Job>()

    fun reduce(action: TaskAction) {
        when (action) {

            is TaskAction.LoadTasks -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        tasks = repository.getTasks()
                    )
                }
            }

            is TaskAction.AddTask -> {
                viewModelScope.launch {
                    val task = Task(
                        id = (_state.value.tasks.maxOfOrNull { it.id } ?: 0) + 1,
                        text = action.text,
                        isDone = false
                    )
                    repository.addTask(task)
                    reduce(TaskAction.LoadTasks)
                }
            }

            is TaskAction.UpdateTaskStatus -> {
                viewModelScope.launch {
                    val task = _state.value.tasks.first { it.id == action.taskId }
                        .copy(isDone = action.isDone)

                    repository.updateTask(task)

                    if (action.isDone) {
                        completeTaskUseCase.execute(
                            scope = viewModelScope,
                            taskId = task.id
                        ) { job ->
                            autoDeleteJobs[task.id] = job
                        }
                    } else {
                        autoDeleteJobs[task.id]?.cancel()
                        autoDeleteJobs.remove(task.id)
                    }

                    reduce(TaskAction.LoadTasks)
                }
            }

            is TaskAction.DeleteTask -> {
                viewModelScope.launch {
                    autoDeleteJobs[action.taskId]?.cancel()
                    autoDeleteJobs.remove(action.taskId)
                    repository.deleteTask(action.taskId)
                    reduce(TaskAction.LoadTasks)
                }
            }
        }
    }
}