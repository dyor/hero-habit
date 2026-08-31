package com.dyor.habithero.data.source.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.dyor.habithero.data.source.local.dao.ComicCoverDao
import com.dyor.habithero.data.source.local.dao.CreditTransactionDao
import com.dyor.habithero.data.source.local.dao.ExampleDao
import com.dyor.habithero.data.source.local.dao.GenerationOutputDao
import com.dyor.habithero.data.source.local.dao.HabitDao
import com.dyor.habithero.data.source.local.entity.ComicCoverEntity
import com.dyor.habithero.data.source.local.entity.CreditTransactionEntity
import com.dyor.habithero.data.source.local.entity.ExampleEntity
import com.dyor.habithero.data.source.local.entity.GenerationOutputEntity
import com.dyor.habithero.data.source.local.entity.HabitEntity

// The app's Room 3 database (lives in commonMain). Bump `version` and add a Migration when you
// change the schema after shipping; otherwise DatabaseModule drops all tables on schema change.
@Database(
    entities = [ComicCoverEntity::class, HabitEntity::class, GenerationOutputEntity::class, CreditTransactionEntity::class, ExampleEntity::class],
    version = 2,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun generationOutputDao(): GenerationOutputDao
    abstract fun creditTransactionDao(): CreditTransactionDao
    abstract fun exampleDao(): ExampleDao

    abstract fun habitDao(): HabitDao
    abstract fun comicCoverDao(): ComicCoverDao
    // Add new DAOs above — make_local.sh inserts here.
}

// Each platform supplies a DatabaseProvider actual that picks the right SQLite driver
// (bundled native on Android/iOS/JVM, web-worker on js/wasmJs).
interface DatabaseProvider {
    fun provideAppDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
