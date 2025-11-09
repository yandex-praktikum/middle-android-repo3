package ru.yandex.architectureproject.presentation.state

sealed interface TaskAction {
    data object LoadTasks : TaskAction
    data class AddTask(val task: String) : TaskAction
    data class UpdateTaskStatus(val taskId: Int, val done: Boolean) : TaskAction
    data class DeleteTask(val taskId: Int) : TaskAction
}
