package com.example.roomdatabase.database.bean

import android.os.Parcel
import android.os.Parcelable
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
    @ColumnInfo(name = "date_of_birth") val date: Long,
    @ColumnInfo(name = "student_image") val image: String
    /* @TypeConverters(Converters::class)
     @ColumnInfo(name = "date") val datePicker: LocalDate?*/
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(gender)
        parcel.writeLong(date)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SampleTable> {
        override fun createFromParcel(parcel: Parcel): SampleTable {
            return SampleTable(parcel)
        }

        override fun newArray(size: Int): Array<SampleTable?> {
            return arrayOfNulls(size)
        }
    }
}
