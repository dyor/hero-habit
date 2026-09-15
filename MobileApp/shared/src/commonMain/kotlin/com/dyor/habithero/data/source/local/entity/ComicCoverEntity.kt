@file:OptIn(ExperimentalUuidApi::class)

package com.dyor.habithero.data.source.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.domain.model.HeroRole
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "comic_cover")
data class ComicCoverEntity(
    @PrimaryKey @ColumnInfo("id") val id: String = Uuid.random().toString(),
    @ColumnInfo("habit_id") val habitId: String,
    @ColumnInfo("habit_title") val habitTitle: String,
    @ColumnInfo("streak_number") val streakNumber: Int = 1,
    @ColumnInfo("headline") val headline: String = "",
    @ColumnInfo("image_url") val imageUrl: String = "",
    @ColumnInfo("hero_role") val heroRole: String = HeroRole.SUPERHERO,
    @ColumnInfo("created_at") val createdAt: Long = 0L,
)

fun ComicCoverEntity.toModel(): ComicCover = ComicCover(
    id = id,
    habitId = habitId,
    habitTitle = habitTitle,
    streakNumber = streakNumber,
    headline = headline,
    imageUrl = imageUrl,
    heroRole = heroRole,
    createdAt = createdAt,
)

fun ComicCover.toEntity(): ComicCoverEntity = ComicCoverEntity(
    id = id,
    habitId = habitId,
    habitTitle = habitTitle,
    streakNumber = streakNumber,
    headline = headline,
    imageUrl = imageUrl,
    heroRole = heroRole,
    createdAt = createdAt,
)
