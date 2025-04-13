package ru.yandex.architectureproject.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.yandex.architectureproject.domain.AddTaskUseCase
import ru.yandex.architectureproject.domain.CompleteTaskUseCase
import ru.yandex.architectureproject.domain.DeleteTaskUseCase
import ru.yandex.architectureproject.domain.GetAllTasksUseCase
import ru.yandex.architectureproject.domain.IncompleteTaskUseCase
import ru.yandex.architectureproject.presentation.state.TaskAction
import ru.yandex.architectureproject.presentation.state.TaskState

class TaskViewModel(
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val incompleteTaskUseCase: IncompleteTaskUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _state = MutableStateFlow<TaskState>(TaskState.Loading)
    val state: StateFlow<TaskState> = _state.asStateFlow()

    private val deletionJobs = mutableMapOf<Int, Job>()

    init {
        viewModelScope.launch {
            reduce(TaskAction.LoadTasks)
        }
    }

    fun reduce(action: TaskAction) {
        viewModelScope.launch {
            when (action) {
                is TaskAction.LoadTasks -> loadTasks()
                is TaskAction.AddTask -> handleAddTask(action)
                is TaskAction.UpdateTaskStatus -> handleUpdateStatus(action)
                is TaskAction.DeleteTask -> handleDeleteTask(action)
            }
        }
    }

    private suspend fun handleAddTask(action: TaskAction.AddTask) {
        addTaskUseCase(action.task)
        loadTasks()
    }

    private suspend fun handleUpdateStatus(action: TaskAction.UpdateTaskStatus) {
        if (action.isDone) {
            // Cancel any existing deletion job for this task
            deletionJobs[action.taskId]?.cancel()

            // Create new deletion job
            val deletionJob = viewModelScope.launch(ioDispatcher) {
                completeTaskUseCase(action.taskId)
                loadTasks()

                // Schedule deletion after 10 seconds
                kotlinx.coroutines.delay(10_000)

                // Check if job wasn't cancelled before proceeding with deletion
                if (isActive) {
                    deleteTaskUseCase(action.taskId)
                    deletionJobs.remove(action.taskId)
                    loadTasks()
                }
            }

            deletionJobs[action.taskId] = deletionJob
        } else {
            // Cancel pending deletion
            deletionJobs[action.taskId]?.cancel()
            deletionJobs.remove(action.taskId)

            incompleteTaskUseCase(action.taskId)
            loadTasks()
        }
    }

    private suspend fun handleDeleteTask(action: TaskAction.DeleteTask) {
        deletionJobs[action.taskId]?.cancel()
        deletionJobs.remove(action.taskId)

        deleteTaskUseCase(action.taskId)
        loadTasks()
    }

    private suspend fun loadTasks() {
        withContext(ioDispatcher) {
            getAllTasksUseCase()
                .distinctUntilChanged()
                .onStart { _state.value = TaskState.Loading }
                .catch { e -> _state.value = TaskState.Error(e.message ?: "Ошибка загрузки") }
                .collect { tasks -> _state.value = TaskState.Loaded(tasks) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel all pending deletion jobs
        deletionJobs.values.forEach { it.cancel() }
        deletionJobs.clear()
    }
}