package ru.yandex.architectureproject

import ru.yandex.architectureproject.model.Task

interface TaskRepository {
    suspend fun getTasks(): List<Task>
    suspend fun addTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: Int)
}


class InMemoryTaskRepository : TaskRepository {

    private val tasks = mutableListOf<Task>()

    override suspend fun getTasks(): List<Task> = tasks.toList()

    override suspend fun addTask(task: Task) {
        tasks.add(task)
    }

    override suspend fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) tasks[index] = task
    }

    override suspend fun deleteTask(taskId: Int) {
        tasks.removeAll { it.id == taskId }
    }
}