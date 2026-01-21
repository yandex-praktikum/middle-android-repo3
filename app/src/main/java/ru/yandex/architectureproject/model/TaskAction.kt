package ru.yandex.architectureproject.model

sealed class TaskAction {
    object LoadTasks : TaskAction()
    data class AddTask(val text: String) : TaskAction()
    data class UpdateTaskStatus(val taskId: Int, val isDone: Boolean) : TaskAction()
    data class DeleteTask(val taskId: Int) : TaskAction()
}