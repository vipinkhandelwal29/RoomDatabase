package com.example.roomdatabase.database.bean

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.adapters.Converters
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class StudentTable(
    @SerializedName("id") val id: Long ,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("date_of_birth") val date: Long,
    @SerializedName("image") val image: String
)