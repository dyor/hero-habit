@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dyor.habithero.presentation.screens.onboarding

import com.dyor.habithero.data.BackgroundExecutor
import com.dyor.habithero.data.repository.HabitRepository
import com.dyor.habithero.data.source.local.dao.ComicCoverDao
import com.dyor.habithero.data.source.local.dao.HabitDao
import com.dyor.habithero.data.source.local.entity.ComicCoverEntity
import com.dyor.habithero.data.source.local.entity.HabitEntity
import com.dyor.habithero.data.source.preferences.FakeUserPreferences
import com.dyor.habithero.data.source.preferences.UserPreferences
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

class OnBoardingViewModelTest {
    private val mainDispatcher = StandardTestDispatcher()
    private lateinit var userPreferences: FakeUserPreferences
    private lateinit var habitDao: FakeHabitDao
    private lateinit var comicCoverDao: FakeComicCoverDao
    private lateinit var habitRepository: HabitRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainDispatcher)
        userPreferences = FakeUserPreferences()
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
    fun `first launch shows onboarding with 9 habit presets and 3 preselected`() = runTest(mainDispatcher) {
        val viewModel = OnBoardingViewModel(userPreferences, habitRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isOnBoardingFinished)
        assertFalse(state.isLoading)
        assertEquals(9, state.habitPresets.size)
        assertEquals(3, state.habitPresets.count { it.isSelected })
    }

    @Test
    fun `returning user finishes as an existing user`() = runTest(mainDispatcher) {
        userPreferences.putBoolean(UserPreferences.KEY_IS_ONBOARD_SHOWN, true)

        val viewModel = OnBoardingViewModel(userPreferences, habitRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isOnBoardingFinished)
        assertFalse(state.isNewUser)
    }

    @Test
    fun `toggling habit preset updates its selection state`() = runTest(mainDispatcher) {
        val viewModel = OnBoardingViewModel(userPreferences, habitRepository)
        advanceUntilIdle()

        val initialSelectedCount = viewModel.uiState.value.habitPresets.count { it.isSelected }

        // Toggle "practice_spanish" (initially unselected)
        viewModel.onUiEvent(OnBoardingUiEvent.OnToggleHabit("practice_spanish"))
        advanceUntilIdle()

        assertEquals(
            initialSelectedCount + 1,
            viewModel.uiState.value.habitPresets.count { it.isSelected },
        )
        assertTrue(
            viewModel.uiState.value.habitPresets.first { it.id == "practice_spanish" }.isSelected,
        )
    }

    @Test
    fun `clicking start creates selected habits and finishes onboarding as a new user`() = runTest(mainDispatcher) {
        val viewModel = OnBoardingViewModel(userPreferences, habitRepository)
        advanceUntilIdle()

        viewModel.onUiEvent(OnBoardingUiEvent.OnClickStart)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isOnBoardingFinished)
        assertTrue(state.isNewUser)
        assertFalse(state.navigateToCreateQuest)
        assertTrue(userPreferences.getBoolean(UserPreferences.KEY_IS_ONBOARD_SHOWN))

        // Check habits were saved to repository
        val savedHabits = habitDao.getAll()
        assertEquals(3, savedHabits.size)
    }

    @Test
    fun `clicking create your own quest creates selected habits and sets navigateToCreateQuest`() = runTest(mainDispatcher) {
        val viewModel = OnBoardingViewModel(userPreferences, habitRepository)
        advanceUntilIdle()

        viewModel.onUiEvent(OnBoardingUiEvent.OnClickCreateYourOwnQuest)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isOnBoardingFinished)
        assertTrue(state.isNewUser)
        assertTrue(state.navigateToCreateQuest)
        assertTrue(userPreferences.getBoolean(UserPreferences.KEY_IS_ONBOARD_SHOWN))

        val savedHabits = habitDao.getAll()
        assertEquals(3, savedHabits.size)
    }

    @Test
    fun `onFinishHandled resets finished and navigateToCreateQuest flags`() = runTest(mainDispatcher) {
        val viewModel = OnBoardingViewModel(userPreferences, habitRepository)
        advanceUntilIdle()

        viewModel.onUiEvent(OnBoardingUiEvent.OnClickCreateYourOwnQuest)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isOnBoardingFinished)
        assertTrue(viewModel.uiState.value.navigateToCreateQuest)

        viewModel.onFinishHandled()
        assertFalse(viewModel.uiState.value.isOnBoardingFinished)
        assertFalse(viewModel.uiState.value.navigateToCreateQuest)
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

    override fun getByHabitIdFlow(habitId: String): Flow<List<ComicCoverEntity>> =
        items.asStateFlow().map { map -> map.values.filter { it.habitId == habitId } }

    override suspend fun getByHabitId(habitId: String): List<ComicCoverEntity> =
        items.value.values.filter { it.habitId == habitId }

    override suspend fun updateStreakNumberAndHeadline(id: String, streakNumber: Int, headline: String) {
        items.value[id]?.let {
            items.value = items.value + (id to it.copy(streakNumber = streakNumber, headline = headline))
        }
    }

    override suspend fun updateCreatedAt(id: String, createdAt: Long) {
        items.value[id]?.let {
            items.value = items.value + (id to it.copy(createdAt = createdAt))
        }
    }

    override suspend fun updateHabitTitleForHabit(habitId: String, habitTitle: String) {
        val updated = items.value.toMutableMap()
        for ((k, v) in items.value) {
            if (v.habitId == habitId) {
                updated[k] = v.copy(habitTitle = habitTitle)
            }
        }
        items.value = updated
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
