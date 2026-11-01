package com.studentsecurevpn.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.studentsecurevpn.data.local.dao.ServerDao
import com.studentsecurevpn.data.local.dao.SettingsDao
import com.studentsecurevpn.data.local.entity.ServerEntity
import com.studentsecurevpn.data.local.entity.SettingEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Room database holding server metadata and user settings. */
@Database(entities = [ServerEntity::class, SettingEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "student_secure_vpn.db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed policy-safe demo rows. They do not contain real credentials.
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).serverDao().upsertAll(seedServers())
                        }
                    }
                })
                .build()
                .also { INSTANCE = it }
        }

        private fun seedServers(): List<ServerEntity> = listOf(
            ServerEntity("free-us-1", "Campus Free US", "United States", "New York", false, 42, 37, null),
            ServerEntity("free-nl-1", "Campus Free NL", "Netherlands", "Amsterdam", false, 58, 45, null),
            ServerEntity("premium-jp-1", "Premium Japan", "Japan", "Tokyo", true, 24, 18, null),
            ServerEntity("premium-de-1", "Premium Germany", "Germany", "Frankfurt", true, 31, 22, null)
        )
    }
}
