package com.jaylangkung.korem.utils.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jaylangkung.korem.utils.room.Logger

@Dao
interface LoggerDao {
    @Query("SELECT * FROM Logger")
    fun getAllLog(): List<Logger>

    @Insert
    fun insert(vararg books: Logger)
}