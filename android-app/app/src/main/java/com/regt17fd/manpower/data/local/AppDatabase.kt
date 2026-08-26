package com.regt17fd.manpower.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.regt17fd.manpower.data.local.dao.AttendanceDao
import com.regt17fd.manpower.data.local.dao.AuditDao
import com.regt17fd.manpower.data.local.dao.PersonnelDao
import com.regt17fd.manpower.data.local.dao.UserDao
import com.regt17fd.manpower.data.local.entity.AttendanceRecord
import com.regt17fd.manpower.data.local.entity.AuditLog
import com.regt17fd.manpower.data.local.entity.Personnel
import com.regt17fd.manpower.data.local.entity.User
import com.regt17fd.manpower.data.seed.SampleDataSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [User::class, Personnel::class, AttendanceRecord::class, AuditLog::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun personnelDao(): PersonnelDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun auditDao(): AuditDao

    companion object {
        private const val DB_NAME = "manpower_encrypted.db"

        /**
         * SQLCipher passphrase for the local DB file.
         *
         * MILESTONE 2 TODO: this build-time constant is a placeholder. Real
         * hardening derives/stores the key via Android Keystore-backed
         * EncryptedSharedPreferences (or a device-bound key generated on
         * first run) so the passphrase never lives in source control. Noted
         * explicitly here — see android-app/README.md "Known limitations".
         */
        private const val PASSPHRASE = "17FdRegtArty-ManpowerDB-DevKey"

        @Volatile private var INSTANCE: AppDatabase? = null

        fun build(context: Context): AppDatabase {
            INSTANCE?.let { return it }
            SQLiteDatabase.loadLibs(context)
            val factory = SupportFactory(SQLiteDatabase.getBytes(PASSPHRASE.toCharArray()))
            val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                .openHelperFactory(factory)
                .addCallback(object : RoomDatabase.Callback() {
                    // Room only calls this once, the first time the underlying
                    // SQLite file is actually created — safe point to seed.
                    // INSTANCE is already assigned below by the time the first
                    // real query triggers file creation, since Room opens the
                    // DB lazily rather than inside databaseBuilder().build().
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { SampleDataSeeder.seed(it) }
                        }
                    }
                })
                .build()
            INSTANCE = instance
            return instance
        }
    }
}
