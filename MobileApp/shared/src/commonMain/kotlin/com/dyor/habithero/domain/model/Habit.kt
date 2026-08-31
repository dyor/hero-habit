package com.dyor.habithero.domain.model

data class Habit(
    val id: String,
    val title: String,
    val category: String = "General",
    val streakCount: Int = 0,
    val bestStreak: Int = 0,
    val lastCompletedDate: String = "",
    val customPrompt: String = "",
    val createdAt: Long = 0L,
)
