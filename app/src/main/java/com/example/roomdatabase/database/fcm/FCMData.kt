package com.example.roomdatabase.database.fcm

import com.google.gson.annotations.SerializedName

data class FCMData(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("dob") val dob: Long,
    @SerializedName("address") val address: String,
    @SerializedName("image") val image: String,
    @SerializedName("token") val token: String,
    @SerializedName("operationaltype") val operationaltype: String
) {
}