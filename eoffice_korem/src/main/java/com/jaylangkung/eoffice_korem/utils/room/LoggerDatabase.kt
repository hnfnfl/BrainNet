package com.jaylangkung.korem.utils.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jaylangkung.korem.utils.room.Logger
import com.jaylangkung.korem.utils.room.LoggerDao

@Database(entities = [Logger::class], version = 1)
abstract class LoggerDatabase : RoomDatabase() {
    abstract fun loggerDao(): LoggerDao
}