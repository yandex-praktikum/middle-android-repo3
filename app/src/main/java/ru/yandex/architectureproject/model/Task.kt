package ru.yandex.architectureproject.model

data class Task(
    val id: Int,
    val text: String,
    val isDone: Boolean
)