@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package com.dyor.habithero.data.repository

import com.dyor.habithero.data.BackgroundExecutor
import com.dyor.habithero.data.source.local.dao.ComicCoverDao
import com.dyor.habithero.data.source.local.dao.HabitDao
import com.dyor.habithero.data.source.local.entity.toEntity
import com.dyor.habithero.data.source.local.entity.toModel
import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.domain.model.Habit
import com.dyor.habithero.domain.model.HeroRole
import com.dyor.habithero.util.extensions.asFormattedDate
import com.dyor.habithero.util.logging.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class HabitRepository(
    private val habitDao: HabitDao,
    private val comicCoverDao: ComicCoverDao,
    private val backgroundExecutor: BackgroundExecutor,
) {
    fun observeAllHabits(): Flow<Result<List<Habit>>> = habitDao.getAllFlow()
        .map { entities -> Result.success(entities.map { it.toModel() }) }
        .catch { error ->
            AppLogger.e("Error observing habits: $error")
            emit(Result.failure(error))
        }

    fun observeHabitById(id: String): Flow<Result<Habit?>> = habitDao.getByIdFlow(id)
        .map { entity -> Result.success(entity?.toModel()) }
        .catch { error ->
            AppLogger.e("Error observing habit $id: $error")
            emit(Result.failure(error))
        }

    suspend fun getHabitById(id: String): Result<Habit> = backgroundExecutor.execute {
        val entity = habitDao.getById(id)
            ?: return@execute Result.failure(Exception("Habit not found with id: $id"))
        Result.success(entity.toModel())
    }

    suspend fun createHabit(title: String, category: String = "General", customPrompt: String = ""): Result<Habit> = backgroundExecutor.execute {
        val habit = Habit(
            id = Uuid.random().toString(),
            title = title.trim(),
            category = category.trim(),
            streakCount = 0,
            bestStreak = 0,
            lastCompletedDate = "",
            customPrompt = customPrompt.trim(),
            createdAt = Clock.System.now().toEpochMilliseconds(),
        )
        habitDao.upsert(habit.toEntity())
        Result.success(habit)
    }

    suspend fun updateHabitTitle(id: String, newTitle: String): Result<Unit> = backgroundExecutor.execute {
        val trimmed = newTitle.trim()
        if (trimmed.isNotBlank()) {
            habitDao.updateTitle(id, trimmed)
            comicCoverDao.updateHabitTitleForHabit(id, trimmed)
        }
        Result.success(Unit)
    }

    suspend fun updateCustomPrompt(id: String, customPrompt: String): Result<Unit> = backgroundExecutor.execute {
        habitDao.updateCustomPrompt(id, customPrompt.trim())
        Result.success(Unit)
    }

    suspend fun updateLastCompletedDate(id: String, lastCompletedDate: String): Result<Unit> = backgroundExecutor.execute {
        habitDao.updateLastCompletedDate(id, lastCompletedDate.trim())
        Result.success(Unit)
    }

    suspend fun completeHabit(id: String): Result<Habit> = backgroundExecutor.execute {
        val habit = habitDao.getById(id)
            ?: return@execute Result.failure(Exception("Habit not found with id: $id"))

        val now = Clock.System.now().toEpochMilliseconds()
        val tz = TimeZone.currentSystemDefault()
        val todayDate = Clock.System.now().toLocalDateTime(tz).date

        // Check if already completed today
        val entries = comicCoverDao.getByHabitId(id)
        val hasEntryToday = entries.any { entry ->
            Instant.fromEpochMilliseconds(entry.createdAt).toLocalDateTime(tz).date == todayDate
        }

        if (!hasEntryToday) {
            // Always create a check-in entry in database
            val entry = ComicCover(
                id = Uuid.random().toString(),
                habitId = id,
                habitTitle = habit.title,
                streakNumber = habit.streakCount + 1,
                headline = "Daily Habit Check-In",
                imageUrl = "",
                heroRole = habit.category,
                createdAt = now,
            )
            comicCoverDao.upsert(entry.toEntity())
        }

        recalculateStreakForHabit(id)

        val updated = habitDao.getById(id) ?: habit
        Result.success(updated.toModel())
    }

    suspend fun saveOrUpdateComicCoverForToday(
        habitId: String,
        habitTitle: String,
        streakNumber: Int,
        headline: String,
        imageUrl: String,
        heroRole: String = HeroRole.SUPERHERO,
    ): Result<ComicCover> = backgroundExecutor.execute {
        val tz = TimeZone.currentSystemDefault()
        val todayDate = Clock.System.now().toLocalDateTime(tz).date
        val now = Clock.System.now().toEpochMilliseconds()
        val entries = comicCoverDao.getByHabitId(habitId)

        // Find if an entry already exists for today
        val existingTodayEntry = entries.firstOrNull { entry ->
            Instant.fromEpochMilliseconds(entry.createdAt).toLocalDateTime(tz).date == todayDate
        }

        val cover = if (existingTodayEntry != null) {
            existingTodayEntry.toModel().copy(
                habitTitle = habitTitle,
                streakNumber = streakNumber,
                headline = headline,
                imageUrl = imageUrl,
                heroRole = heroRole,
                createdAt = now,
            )
        } else {
            ComicCover(
                id = Uuid.random().toString(),
                habitId = habitId,
                habitTitle = habitTitle,
                streakNumber = streakNumber,
                headline = headline,
                imageUrl = imageUrl,
                heroRole = heroRole,
                createdAt = now,
            )
        }

        comicCoverDao.upsert(cover.toEntity())

        // Clean up any extra empty/duplicate entries for today for this habit
        val allTodayEntries = comicCoverDao.getByHabitId(habitId).filter {
            Instant.fromEpochMilliseconds(it.createdAt).toLocalDateTime(tz).date == todayDate
        }
        for (extra in allTodayEntries) {
            if (extra.id != cover.id && extra.imageUrl.isBlank()) {
                comicCoverDao.deleteById(extra.id)
            }
        }

        recalculateStreakForHabit(habitId)
        Result.success(cover)
    }

    suspend fun addPastEntry(id: String, timestampMillis: Long): Result<Habit> = backgroundExecutor.execute {
        val habit = habitDao.getById(id)
            ?: return@execute Result.failure(Exception("Habit not found with id: $id"))

        val entry = ComicCover(
            id = Uuid.random().toString(),
            habitId = id,
            habitTitle = habit.title,
            streakNumber = habit.streakCount + 1,
            headline = "Logged Check-In",
            imageUrl = "",
            heroRole = habit.category,
            createdAt = timestampMillis,
        )
        comicCoverDao.upsert(entry.toEntity())

        recalculateStreakForHabit(id)
        val updated = habitDao.getById(id) ?: habit
        Result.success(updated.toModel())
    }

    suspend fun updateEntryDate(coverId: String, newTimestampMillis: Long): Result<Unit> = backgroundExecutor.execute {
        val entry = comicCoverDao.getById(coverId) ?: return@execute Result.failure(Exception("Entry not found"))
        comicCoverDao.updateCreatedAt(coverId, newTimestampMillis)
        recalculateStreakForHabit(entry.habitId)
        Result.success(Unit)
    }

    suspend fun deleteEntry(coverId: String): Result<Unit> = backgroundExecutor.execute {
        val entry = comicCoverDao.getById(coverId) ?: return@execute Result.failure(Exception("Entry not found"))
        val habitId = entry.habitId
        comicCoverDao.deleteById(coverId)
        recalculateStreakForHabit(habitId)
        Result.success(Unit)
    }

    suspend fun deleteHabit(id: String): Result<Unit> = backgroundExecutor.execute {
        comicCoverDao.deleteByHabitId(id)
        habitDao.deleteById(id)
        Result.success(Unit)
    }

    suspend fun recalculateStreakForHabit(habitId: String) {
        val habit = habitDao.getById(habitId) ?: return
        val entries = comicCoverDao.getByHabitId(habitId)

        if (entries.isEmpty()) {
            val updated = habit.copy(
                streakCount = 0,
                lastCompletedDate = "",
            )
            habitDao.upsert(updated)
            return
        }

        val tz = TimeZone.currentSystemDefault()
        val entryDates = entries.map { entry ->
            Instant.fromEpochMilliseconds(entry.createdAt).toLocalDateTime(tz).date
        }.toSet()

        val today = Clock.System.now().toLocalDateTime(tz).date
        val yesterday = today.minus(1, DateTimeUnit.DAY)

        // Calculate current streak
        var currentStreak = 0
        var testDate: LocalDate? = when {
            entryDates.contains(today) -> today
            entryDates.contains(yesterday) -> yesterday
            else -> null
        }

        while (testDate != null && entryDates.contains(testDate)) {
            currentStreak++
            testDate = testDate.minus(1, DateTimeUnit.DAY)
        }

        // Calculate best streak, and the day-within-its-streak for every entry date.
        val sortedDates = entryDates.sorted()
        val dayInStreakByDate = mutableMapOf<LocalDate, Int>()
        var maxStreak = 0
        var tempStreak = 0
        var prevDate: LocalDate? = null

        for (date in sortedDates) {
            if (prevDate == null || date == prevDate.plus(1, DateTimeUnit.DAY)) {
                tempStreak++
            } else {
                tempStreak = 1
            }
            dayInStreakByDate[date] = tempStreak
            if (tempStreak > maxStreak) {
                maxStreak = tempStreak
            }
            prevDate = date
        }

        // Entries store their day number at capture time, so back-dating or deleting an
        // entry leaves neighbouring entries stale. Re-derive it from the real dates.
        for (entry in entries) {
            val entryDate = Instant.fromEpochMilliseconds(entry.createdAt).toLocalDateTime(tz).date
            val day = dayInStreakByDate[entryDate] ?: continue
            val newHeadline = entry.headline.withStreakDay(day)
            if (entry.streakNumber != day || entry.headline != newHeadline) {
                comicCoverDao.updateStreakNumberAndHeadline(entry.id, day, newHeadline)
            }
        }

        val bestStreak = maxOf(habit.bestStreak, maxOf(maxStreak, currentStreak))

        // Determine last completed date string
        val lastDateStr = when {
            entryDates.contains(today) -> "Today"

            entryDates.contains(yesterday) -> "Yesterday"

            else -> {
                val latestEpoch = entries.maxOfOrNull { it.createdAt } ?: 0L
                if (latestEpoch > 0L) latestEpoch.asFormattedDate(tz, "MMM d") else ""
            }
        }

        val updated = habit.copy(
            streakCount = currentStreak,
            bestStreak = bestStreak,
            lastCompletedDate = lastDateStr,
        )
        habitDao.upsert(updated)
    }

    suspend fun seedInitialHabitsIfEmpty() = backgroundExecutor.execute {
        val existing = habitDao.getAll()
        if (existing.isEmpty()) {
            val samples = listOf(
                Habit(id = Uuid.random().toString(), title = "Morning Jog", category = "Fitness", streakCount = 0, bestStreak = 0, customPrompt = "running at lightning speed through bustling futuristic metropolis with glowing neon trails", lastCompletedDate = ""),
                Habit(id = Uuid.random().toString(), title = "Read 20 Mins", category = "Mind", streakCount = 0, bestStreak = 0, customPrompt = "studying ancient holographic scrolls inside a secret galactic library of wisdom", lastCompletedDate = ""),
                Habit(id = Uuid.random().toString(), title = "Drink 2L Water", category = "Health", streakCount = 0, bestStreak = 0, customPrompt = "controlling elemental tidal waves and glowing hydro energy shields", lastCompletedDate = ""),
                Habit(id = Uuid.random().toString(), title = "Meditation", category = "Focus", streakCount = 0, bestStreak = 0, customPrompt = "meditating in epic cosmic places like deep space, floating near nebulae, waterfalls, and towering volcanoes", lastCompletedDate = ""),
            )
            samples.forEach { habitDao.upsert(it.toEntity()) }
        } else {
            // Clean up any duplicate empty entries from earlier check-ins
            cleanupDuplicateEntriesInternal()
            // Recompute streaks for all existing habits from their real entries
            for (habit in existing) {
                recalculateStreakForHabit(habit.id)
            }
        }
        Result.success(Unit)
    }

    private suspend fun cleanupDuplicateEntriesInternal() {
        val allHabits = habitDao.getAll()
        val tz = TimeZone.currentSystemDefault()
        for (habit in allHabits) {
            val entries = comicCoverDao.getByHabitId(habit.id)
            val groupedByDate = entries.groupBy { entry ->
                Instant.fromEpochMilliseconds(entry.createdAt).toLocalDateTime(tz).date
            }
            for ((_, dateEntries) in groupedByDate) {
                if (dateEntries.size > 1) {
                    val hasImage = dateEntries.filter { it.imageUrl.isNotBlank() }
                    if (hasImage.isNotEmpty()) {
                        // Delete all empty entries for this date
                        for (entry in dateEntries) {
                            if (entry.imageUrl.isBlank()) {
                                comicCoverDao.deleteById(entry.id)
                            }
                        }
                    } else {
                        // Keep only the newest empty entry
                        val sorted = dateEntries.sortedByDescending { it.createdAt }
                        for (entry in sorted.drop(1)) {
                            comicCoverDao.deleteById(entry.id)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Rewrites the day number embedded in a comic cover headline, leaving headlines that carry no
 * day number (e.g. "Daily Habit Check-In") untouched. Handles both authored formats:
 * "<Habit> Day 3 Logged!" and "3 Day Streak Hero!".
 */
private fun String.withStreakDay(day: Int): String {
    val dayPrefixed = Regex("""\bDay\s+\d+""")
    val streakSuffixed = Regex("""\b\d+\s+Day\s+Streak""")
    return when {
        dayPrefixed.containsMatchIn(this) -> dayPrefixed.replace(this, "Day $day")
        streakSuffixed.containsMatchIn(this) -> streakSuffixed.replace(this, "$day Day Streak")
        else -> this
    }
}
