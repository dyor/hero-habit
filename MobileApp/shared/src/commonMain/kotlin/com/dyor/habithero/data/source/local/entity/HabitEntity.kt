@file:OptIn(ExperimentalUuidApi::class)

package com.dyor.habithero.data.source.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.dyor.habithero.domain.model.Habit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "habit")
data class HabitEntity(
    @PrimaryKey @ColumnInfo("id") val id: String = Uuid.random().toString(),
    @ColumnInfo("title") val title: String,
    @ColumnInfo("category") val category: String = "General",
    @ColumnInfo("streak_count") val streakCount: Int = 0,
    @ColumnInfo("best_streak") val bestStreak: Int = 0,
    @ColumnInfo("last_completed_date") val lastCompletedDate: String = "",
    @ColumnInfo("custom_prompt") val customPrompt: String = "",
    @ColumnInfo("created_at") val createdAt: Long = 0L,
)

fun HabitEntity.toModel(): Habit = Habit(
    id = id,
    title = title,
    category = category,
    streakCount = streakCount,
    bestStreak = bestStreak,
    lastCompletedDate = lastCompletedDate,
    customPrompt = customPrompt,
    createdAt = createdAt,
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id,
    title = title,
    category = category,
    streakCount = streakCount,
    bestStreak = bestStreak,
    lastCompletedDate = lastCompletedDate,
    customPrompt = customPrompt,
    createdAt = createdAt,
)
