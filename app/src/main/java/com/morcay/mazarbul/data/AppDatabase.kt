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
                    // ✅ IMPORTANTE: registrar TODAS las migraciones necesarias
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * ✅ MIGRATION 1 -> 2
         * Si en la versión 2 no cambiaste el esquema, puede estar VACÍA y es correcta.
         * Si en v2 sí cambiaste algo, aquí irían esos ALTER TABLE.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // No-op (sin cambios de esquema en v2)
            }
        }

        /**
         * ✅ MIGRATION 2 -> 3
         * Añadimos inventario mínimo: armaduraEquipada y tieneEscudo
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE personajes ADD COLUMN armaduraEquipada TEXT NOT NULL DEFAULT 'Sin armadura'"
                )
                db.execSQL(
                    "ALTER TABLE personajes ADD COLUMN tieneEscudo INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE personajes ADD COLUMN armaEquipada TEXT NOT NULL DEFAULT 'Sin arma'")
            }
        }
    }
}

