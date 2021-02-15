package com.example.roomdatabase.database.bean

import androidx.databinding.adapters.Converters
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

@Entity(tableName = "word_table")
data class SampleTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "student_name") val name: String,
    @ColumnInfo(name = "student_address") val address: String,
    @ColumnInfo(name = "student_gender") val gender: String,
    @ColumnInfo(name = "date_of_birth") val date: Long
   /* @TypeConverters(Converters::class)
    @ColumnInfo(name = "date") val datePicker: LocalDate?*/
)
