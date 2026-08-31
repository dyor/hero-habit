package com.dyor.habithero.data.source.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Upsert
import com.dyor.habithero.data.source.local.entity.ComicCoverEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicCoverDao {
    @Query("SELECT * FROM comic_cover WHERE id = :id")
    suspend fun getById(id: String): ComicCoverEntity?

    @Query("SELECT * FROM comic_cover WHERE id = :id")
    fun getByIdFlow(id: String): Flow<ComicCoverEntity?>

    @Query("SELECT * FROM comic_cover ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<ComicCoverEntity>>

    @Query("SELECT * FROM comic_cover ORDER BY created_at DESC")
    suspend fun getAll(): List<ComicCoverEntity>

    @Query("SELECT * FROM comic_cover WHERE habit_id = :habitId ORDER BY created_at DESC")
    fun getByHabitIdFlow(habitId: String): Flow<List<ComicCoverEntity>>

    @Query("SELECT * FROM comic_cover WHERE habit_id = :habitId ORDER BY created_at DESC")
    suspend fun getByHabitId(habitId: String): List<ComicCoverEntity>

    @Query("UPDATE comic_cover SET created_at = :createdAt WHERE id = :id")
    suspend fun updateCreatedAt(id: String, createdAt: Long)

    @Query("UPDATE comic_cover SET habit_title = :habitTitle WHERE habit_id = :habitId")
    suspend fun updateHabitTitleForHabit(habitId: String, habitTitle: String)

    @Upsert
    suspend fun upsert(entity: ComicCoverEntity)

    @Query("DELETE FROM comic_cover WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM comic_cover WHERE habit_id = :habitId")
    suspend fun deleteByHabitId(habitId: String)

    @Delete
    suspend fun delete(entity: ComicCoverEntity)

    @Query("DELETE FROM comic_cover")
    suspend fun deleteAll()
}
