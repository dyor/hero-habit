package com.dyor.habithero.domain.model

data class ComicCover(
    val id: String,
    val habitId: String,
    val habitTitle: String,
    val streakNumber: Int,
    val headline: String,
    val imageUrl: String,
    val heroRole: String = HeroRole.SUPERHERO,
    val createdAt: Long = 0L,
)
