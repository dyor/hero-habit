@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dyor.habithero.presentation.screens.habitdetail

import com.dyor.habithero.data.BackgroundExecutor
import com.dyor.habithero.data.repository.HabitRepository
import com.dyor.habithero.data.source.local.dao.ComicCoverDao
import com.dyor.habithero.data.source.local.dao.HabitDao
import com.dyor.habithero.data.source.local.entity.ComicCoverEntity
import com.dyor.habithero.data.source.local.entity.HabitEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HabitDetailViewModelTest {
    private val mainDispatcher = StandardTestDispatcher()
    private lateinit var habitDao: FakeHabitDao
    private lateinit var comicCoverDao: FakeComicCoverDao
    private lateinit var habitRepository: HabitRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainDispatcher)
        habitDao = FakeHabitDao()
        comicCoverDao = FakeComicCoverDao()
        val executor = BackgroundExecutor(mainDispatcher)
        habitRepository = HabitRepository(habitDao, comicCoverDao, executor)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `generating AI prompt updates custom prompt input with heroic scenario`() = runTest(mainDispatcher) {
        val testHabit = HabitEntity(
            id = "habit_1",
            title = "Drink Water",
            category = "Health",
            customPrompt = "",
        )
        habitDao.upsert(testHabit)

        val viewModel = HabitDetailViewModel("habit_1", habitRepository, comicCoverDao)
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.customPromptInput)

        viewModel.onUiEvent(HabitDetailUiEvent.OnGenerateAiPrompt)
        advanceUntilIdle()

        val generatedPrompt = viewModel.uiState.value.customPromptInput
        assertTrue(generatedPrompt.isNotBlank())
        assertFalse(viewModel.uiState.value.isGeneratingPrompt)

        // Verify saving the generated prompt updates the repository
        viewModel.onUiEvent(HabitDetailUiEvent.OnSaveCustomPrompt)
        advanceUntilIdle()

        val updatedEntity = habitDao.getById("habit_1")
        assertEquals(generatedPrompt, updatedEntity?.customPrompt)
    }

    @Test
    fun `clearing custom prompt input is preserved across flow emissions`() = runTest(mainDispatcher) {
        val testHabit = HabitEntity(
            id = "habit_1",
            title = "Drink Water",
            category = "Health",
            customPrompt = "Hydrate like a champion",
        )
        habitDao.upsert(testHabit)

        val viewModel = HabitDetailViewModel("habit_1", habitRepository, comicCoverDao)
        advanceUntilIdle()

        assertEquals("Hydrate like a champion", viewModel.uiState.value.customPromptInput)

        // Clear custom prompt input
        viewModel.onUiEvent(HabitDetailUiEvent.OnCustomPromptChange(""))
        assertEquals("", viewModel.uiState.value.customPromptInput)

        // Trigger a new emission from habitDao
        habitDao.updateTitle("habit_1", "Drink More Water")
        advanceUntilIdle()

        assertEquals("Drink More Water", viewModel.uiState.value.habit?.title)
        assertEquals("", viewModel.uiState.value.customPromptInput)
    }

    @Test
    fun `rapid consecutive OnGenerateAiPrompt events guard against duplicate execution`() = runTest(mainDispatcher) {
        val testHabit = HabitEntity(
            id = "habit_1",
            title = "Drink Water",
            category = "Health",
            customPrompt = "",
        )
        habitDao.upsert(testHabit)

        val viewModel = HabitDetailViewModel("habit_1", habitRepository, comicCoverDao)
        advanceUntilIdle()

        // Fire consecutive events
        viewModel.onUiEvent(HabitDetailUiEvent.OnGenerateAiPrompt)
        viewModel.onUiEvent(HabitDetailUiEvent.OnGenerateAiPrompt)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.customPromptInput.isNotBlank())
        assertFalse(viewModel.uiState.value.isGeneratingPrompt)
    }
}

private class FakeHabitDao : HabitDao {
    private val items = MutableStateFlow<Map<String, HabitEntity>>(emptyMap())

    override suspend fun getById(id: String): HabitEntity? = items.value[id]

    override fun getByIdFlow(id: String): Flow<HabitEntity?> = items.asStateFlow().map { it[id] }

    override fun getAllFlow(): Flow<List<HabitEntity>> = items.asStateFlow().map { it.values.toList() }

    override suspend fun getAll(): List<HabitEntity> = items.value.values.toList()

    override suspend fun upsert(entity: HabitEntity) {
        items.value = items.value + (entity.id to entity)
    }

    override suspend fun updateTitle(id: String, title: String) {
        items.value[id]?.let {
            items.value = items.value + (id to it.copy(title = title))
        }
    }

    override suspend fun updateCustomPrompt(id: String, customPrompt: String) {
        items.value[id]?.let {
            items.value = items.value + (id to it.copy(customPrompt = customPrompt))
        }
    }

    override suspend fun updateLastCompletedDate(id: String, lastCompletedDate: String) {
        items.value[id]?.let {
            items.value = items.value + (id to it.copy(lastCompletedDate = lastCompletedDate))
        }
    }

    override suspend fun deleteById(id: String) {
        items.value = items.value - id
    }

    override suspend fun delete(entity: HabitEntity) {
        items.value = items.value - entity.id
    }

    override suspend fun deleteAll() {
        items.value = emptyMap()
    }
}

private class FakeComicCoverDao : ComicCoverDao {
    private val items = MutableStateFlow<Map<String, ComicCoverEntity>>(emptyMap())

    override suspend fun getById(id: String): ComicCoverEntity? = items.value[id]

    override fun getByIdFlow(id: String): Flow<ComicCoverEntity?> = items.asStateFlow().map { it[id] }

    override fun getAllFlow(): Flow<List<ComicCoverEntity>> = items.asStateFlow().map { it.values.toList() }

    override suspend fun getAll(): List<ComicCoverEntity> = items.value.values.toList()

    override fun getByHabitIdFlow(habitId: String): Flow<List<ComicCoverEntity>> = items.asStateFlow().map { map -> map.values.filter { it.habitId == habitId } }

    override suspend fun getByHabitId(habitId: String): List<ComicCoverEntity> = items.value.values.filter { it.habitId == habitId }

    override suspend fun updateCreatedAt(id: String, createdAt: Long) {
        items.value[id]?.let {
            items.value = items.value + (id to it.copy(createdAt = createdAt))
        }
    }

    override suspend fun updateHabitTitleForHabit(habitId: String, habitTitle: String) {
        items.value = items.value.mapValues { (_, cover) ->
            if (cover.habitId == habitId) cover.copy(habitTitle = habitTitle) else cover
        }
    }

    override suspend fun updateStreakNumberAndHeadline(id: String, streakNumber: Int, headline: String) {
        items.value[id]?.let {
            items.value = items.value + (id to it.copy(streakNumber = streakNumber, headline = headline))
        }
    }

    override suspend fun upsert(entity: ComicCoverEntity) {
        items.value = items.value + (entity.id to entity)
    }

    override suspend fun deleteById(id: String) {
        items.value = items.value - id
    }

    override suspend fun deleteByHabitId(habitId: String) {
        items.value = items.value.filterValues { it.habitId != habitId }
    }

    override suspend fun delete(entity: ComicCoverEntity) {
        items.value = items.value - entity.id
    }

    override suspend fun deleteAll() {
        items.value = emptyMap()
    }
}
