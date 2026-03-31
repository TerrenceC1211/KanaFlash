package com.vitaminC.kanaflash.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppDatabaseProvider {
    @Volatile
    private var instance: AppDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS decks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title TEXT NOT NULL
                )
                """.trimIndent()
            )

            database.execSQL(
                """
                INSERT INTO decks (id, title) VALUES (1, 'My Deck')
                """.trimIndent()
            )

            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS vocabulary_entries_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    romaji TEXT NOT NULL,
                    hiragana TEXT NOT NULL,
                    meaning TEXT,
                    deckId INTEGER NOT NULL,
                    FOREIGN KEY(deckId) REFERENCES decks(id) ON DELETE CASCADE
                )
                """.trimIndent()
            )

            database.execSQL(
                """
                INSERT INTO vocabulary_entries_new (id, romaji, hiragana, meaning, deckId)
                SELECT id, romaji, hiragana, meaning, 1
                FROM vocabulary_entries
                """.trimIndent()
            )

            database.execSQL("DROP TABLE vocabulary_entries")
            database.execSQL("ALTER TABLE vocabulary_entries_new RENAME TO vocabulary_entries")
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_vocabulary_entries_deckId ON vocabulary_entries(deckId)"
            )
        }
    }

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: buildDatabase(context.applicationContext).also { instance = it }
        }
    }

    private fun buildDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "kanaflash_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}
