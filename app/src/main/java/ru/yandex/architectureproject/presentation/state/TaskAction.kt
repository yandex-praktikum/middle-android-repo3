package ru.yandex.architectureproject.presentation.state

sealed class TaskAction {
    data object LoadTasks : TaskAction()
    data class AddTask(val task: String) : TaskAction()
    data class UpdateTaskStatus(val taskId: Int, val isDone: Boolean) : TaskAction()
    data class DeleteTask(val taskId: Int) : TaskAction()
    // TODO: Здесь должны быть действия (загрузка заданий, добавление задания, обновление статуса задания (сделано/не сделано) и удаление задания)
}
