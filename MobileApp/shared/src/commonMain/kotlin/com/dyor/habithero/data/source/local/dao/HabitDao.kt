package com.dyor.habithero.data.source.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Upsert
import com.dyor.habithero.data.source.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit WHERE id = :id")
    suspend fun getById(id: String): HabitEntity?

    @Query("SELECT * FROM habit WHERE id = :id")
    fun getByIdFlow(id: String): Flow<HabitEntity?>

    @Query("SELECT * FROM habit")
    fun getAllFlow(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit")
    suspend fun getAll(): List<HabitEntity>

    @Upsert
    suspend fun upsert(entity: HabitEntity)

    @Query("UPDATE habit SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: String, title: String)

    @Query("UPDATE habit SET custom_prompt = :customPrompt WHERE id = :id")
    suspend fun updateCustomPrompt(id: String, customPrompt: String)

    @Query("UPDATE habit SET last_completed_date = :lastCompletedDate WHERE id = :id")
    suspend fun updateLastCompletedDate(id: String, lastCompletedDate: String)

    @Query("DELETE FROM habit WHERE id = :id")
    suspend fun deleteById(id: String)

    @Delete
    suspend fun delete(entity: HabitEntity)

    @Query("DELETE FROM habit")
    suspend fun deleteAll()
}
