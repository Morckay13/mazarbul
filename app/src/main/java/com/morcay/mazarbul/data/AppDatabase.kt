package com.morcay.mazarbul.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [PersonajeEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personajeDao(): PersonajeDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mazarbul_db"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()


                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE personajes ADD COLUMN armaduraEquipada TEXT NOT NULL DEFAULT 'Sin armadura'")
                db.execSQL("ALTER TABLE personajes ADD COLUMN tieneEscudo INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}

