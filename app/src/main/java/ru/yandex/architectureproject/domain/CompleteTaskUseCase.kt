package ru.yandex.architectureproject.domain

import ru.yandex.architectureproject.data.repository.TaskRepository
import javax.inject.Inject


class CompleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Int) {
        repository.completeTask(taskId)
    }
}