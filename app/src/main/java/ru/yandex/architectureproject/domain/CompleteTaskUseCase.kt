package ru.yandex.architectureproject.domain

import kotlinx.coroutines.delay
import ru.yandex.architectureproject.data.repository.TaskRepository

class CompleteTaskUseCase(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(taskId: Int) {
        repository.completeTask(taskId)
        delay(DELAY_TIME_MS)
        repository.deleteTask(taskId)
    }

    companion object {
        private const val DELAY_TIME_MS = 10000L
    }
}
