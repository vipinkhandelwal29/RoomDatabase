package com.example.roomdatabase.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomdatabase.database.bean.SampleTable

@Dao
interface SampleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(data: SampleTable): Long

    @Query("SELECT * FROM word_table")
    fun  getData(): LiveData<List<SampleTable>>

   /* @Query("DELETE FROM word_table where id= :id")
    fun delete(id: Int): Boolean

    @Query("DELETE FROM word_table")
    fun deleteAll()*/
}