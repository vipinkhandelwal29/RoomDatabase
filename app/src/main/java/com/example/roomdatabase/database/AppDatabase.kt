package com.example.roomdatabase.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roomdatabase.database.bean.SampleTable
import com.example.roomdatabase.database.dao.SampleDao

@Database(entities = [SampleTable::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sampleDao(): SampleDao

    companion object {
        @Volatile
        private var instances: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instances ?: synchronized(this)
            {
                instances ?: buildDatabase(context).also { instances = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "school_room.sqlite"
            ).addCallback(object : RoomDatabase.Callback() {})
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build()
        }
    }
}