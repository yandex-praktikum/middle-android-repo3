package ru.yandex.architectureproject.domain

import kotlinx.coroutines.delay
import ru.yandex.architectureproject.data.repository.TaskRepository
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(taskId: Int) {
        repository.completeTask(taskId)

        // Запускаем автоудаление через 10 секунд
        delay(10_000) // 10 секунд в миллисекундах
        repository.deleteTask(taskId)
    }
}