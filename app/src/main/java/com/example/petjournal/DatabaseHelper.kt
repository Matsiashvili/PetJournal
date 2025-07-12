package com.example.petjournal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "petjournal.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_NAME = "entries"
        const val COLUMN_ID = "id"
        const val COLUMN_PET_NAME = "petName"
        const val COLUMN_SPECIES = "species"
        const val COLUMN_BREED = "breed"
        const val COLUMN_DATE = "date"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_IMAGE_URL = "imageUrl"
        const val COLUMN_IS_FAVORITE = "isFavorite"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_PET_NAME TEXT NOT NULL,
                $COLUMN_SPECIES TEXT,
                $COLUMN_BREED TEXT,
                $COLUMN_DATE INTEGER,
                $COLUMN_NOTES TEXT,
                $COLUMN_IMAGE_URL TEXT,
                $COLUMN_IS_FAVORITE INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_IS_FAVORITE INTEGER DEFAULT 0")
        }
    }

    fun addEntry(entry: PetJournalEntry): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, entry.id)
            put(COLUMN_PET_NAME, entry.petName)
            put(COLUMN_SPECIES, entry.species)
            put(COLUMN_BREED, entry.breed)
            put(COLUMN_DATE, entry.date)
            put(COLUMN_NOTES, entry.notes)
            put(COLUMN_IMAGE_URL, entry.imageUrl)
            put(COLUMN_IS_FAVORITE, if (entry.isFavorite) 1 else 0)
        }
        return db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllEntries(): List<PetJournalEntry> {
        val entries = mutableListOf<PetJournalEntry>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME, null, null, null, null, null,
            "$COLUMN_DATE DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                entries.add(
                    PetJournalEntry(
                        id = it.getString(it.getColumnIndexOrThrow(COLUMN_ID)),
                        petName = it.getString(it.getColumnIndexOrThrow(COLUMN_PET_NAME)),
                        species = it.getString(it.getColumnIndexOrThrow(COLUMN_SPECIES)),
                        breed = it.getString(it.getColumnIndexOrThrow(COLUMN_BREED)),
                        date = it.getLong(it.getColumnIndexOrThrow(COLUMN_DATE)),
                        notes = it.getString(it.getColumnIndexOrThrow(COLUMN_NOTES)),
                        imageUrl = it.getString(it.getColumnIndexOrThrow(COLUMN_IMAGE_URL)),
                        isFavorite = it.getInt(it.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1
                    )
                )
            }
        }
        return entries
    }

    fun getFavoriteEntries(): List<PetJournalEntry> {
        val entries = mutableListOf<PetJournalEntry>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME, null,
            "$COLUMN_IS_FAVORITE = ?",
            arrayOf("1"), null, null,
            "$COLUMN_DATE DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                entries.add(
                    PetJournalEntry(
                        id = it.getString(it.getColumnIndexOrThrow(COLUMN_ID)),
                        petName = it.getString(it.getColumnIndexOrThrow(COLUMN_PET_NAME)),
                        species = it.getString(it.getColumnIndexOrThrow(COLUMN_SPECIES)),
                        breed = it.getString(it.getColumnIndexOrThrow(COLUMN_BREED)),
                        date = it.getLong(it.getColumnIndexOrThrow(COLUMN_DATE)),
                        notes = it.getString(it.getColumnIndexOrThrow(COLUMN_NOTES)),
                        imageUrl = it.getString(it.getColumnIndexOrThrow(COLUMN_IMAGE_URL)),
                        isFavorite = it.getInt(it.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1
                    )
                )
            }
        }
        return entries
    }

    fun updateFavoriteStatus(id: String, isFavorite: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_FAVORITE, if (isFavorite) 1 else 0)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id))
    }
}