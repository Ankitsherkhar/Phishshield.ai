package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.engine.PhishDetectorEngine
import com.example.model.ScanResult
import com.example.model.ScanType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Room Database managing device-local security audit persistence.
 */
@Database(
    entities = [ScanResultEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ScanConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scanResultDao(): ScanResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "phishshield_database"
                )
                .addCallback(DatabaseCallback(coroutineScope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun createInMemoryDatabase(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                AppDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedInitialDemoData(database.scanResultDao())
                    }
                }
            }
        }

        suspend fun seedInitialDemoData(dao: ScanResultDao) {
            val initialDemoScans = generateInitialDemoScans()
            dao.insertAll(initialDemoScans.map { it.toEntity() })
        }

        fun generateInitialDemoScans(): List<ScanResult> {
            val baseTime = System.currentTimeMillis()
            val demo1 = PhishDetectorEngine.analyze(
                type = ScanType.EMAIL,
                subject = "ACTION REQUIRED: Security Alert on PayPal Account",
                content = "Dear Customer,\n\nWe detected suspicious unauthorized login attempts from a new IP address (Moscow, RU). Your account has been temporarily restricted.\n\nYou must verify your password and identity within 24 hours at http://paypa1-verify-billing.xyz/login to prevent account termination.",
                isPresetDemo = true,
                timestamp = baseTime - (60 * 60 * 1000L) // 60 min ago
            )

            val demo2 = PhishDetectorEngine.analyze(
                type = ScanType.SMS,
                content = "USPS Notice: Your package delivery has been put on hold due to missing house number. Confirm your address and pay $1.50 redelivery fee at https://usps-track-package.top/claim",
                isPresetDemo = true,
                timestamp = baseTime - (45 * 60 * 1000L) // 45 min ago
            )

            val demo3 = PhishDetectorEngine.analyze(
                type = ScanType.URL,
                content = "http://wellsfarg0-online-secure-auth.club/login",
                isPresetDemo = true,
                timestamp = baseTime - (30 * 60 * 1000L) // 30 min ago
            )

            val demo4 = PhishDetectorEngine.analyze(
                type = ScanType.EMAIL,
                subject = "Weekly Engineering Team Sync & Agenda",
                content = "Hi team,\n\nHere is our agenda for Thursday's bi-weekly sync. We will review sprint metrics and discuss the new cloud migration timeline. See you in conference room B.\n\nBest,\nMarcus",
                isPresetDemo = true,
                timestamp = baseTime - (15 * 60 * 1000L) // 15 min ago
            )

            val demo5 = PhishDetectorEngine.analyze(
                type = ScanType.SMS,
                content = "Hey Alex, are we still good for coffee at 2 PM today? Let me know when you're nearby!",
                isPresetDemo = true,
                timestamp = baseTime // most recent
            )

            // Return in default descending order: demo5 (newest), demo4, demo3, demo2, demo1 (oldest)
            return listOf(demo5, demo4, demo3, demo2, demo1)
        }
    }
}
