package com.example.roomdatabase.database.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudentTable(
    @SerializedName("id")val id:Long,
    @SerializedName("name") val name:String,
    @SerializedName("gender") val gender:String,
    @SerializedName("dob") val dob:Long,
    @SerializedName("address") val address:String,
    @SerializedName("image") val image:String,
    @SerializedName("token") val token:String

    ): Parcelable
