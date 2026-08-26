package com.regt17fd.manpower.di

import android.content.Context
import com.regt17fd.manpower.data.local.AppDatabase
import com.regt17fd.manpower.data.local.dao.AttendanceDao
import com.regt17fd.manpower.data.local.dao.AuditDao
import com.regt17fd.manpower.data.local.dao.PersonnelDao
import com.regt17fd.manpower.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.build(context)

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun providePersonnelDao(db: AppDatabase): PersonnelDao = db.personnelDao()
    @Provides fun provideAttendanceDao(db: AppDatabase): AttendanceDao = db.attendanceDao()
    @Provides fun provideAuditDao(db: AppDatabase): AuditDao = db.auditDao()
}
