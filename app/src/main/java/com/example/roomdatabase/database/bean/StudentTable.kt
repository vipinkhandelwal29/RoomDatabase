package com.example.roomdatabase.database.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudentTable(
    @SerializedName("id")val id:Long,
    @SerializedName("student_name") val name:String,
    @SerializedName("student_gender") val gender:String,
    @SerializedName("student_dob") val dob:Long,
    @SerializedName("student_address") val address:String,
    @SerializedName("student_image") val image:String
): Parcelable
