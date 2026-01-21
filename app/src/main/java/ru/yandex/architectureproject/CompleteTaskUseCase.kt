package ru.yandex.architectureproject

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CompleteTaskUseCase(
    private val repository: TaskRepository
) {

    fun execute(
        scope: CoroutineScope,
        taskId: Int,
        onJobCreated: (Job) -> Unit
    ) {
        val job = scope.launch {
            delay(10_000)
            repository.deleteTask(taskId)
        }
        onJobCreated(job)
    }
}