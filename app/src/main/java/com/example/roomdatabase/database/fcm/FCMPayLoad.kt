package com.example.roomdatabase.database.fcm

import com.google.gson.annotations.SerializedName

data class FCMPayLoad(
    @SerializedName("data") val data: FCMData,
    @SerializedName("registration") val registration_id: ArrayList<String>
) {}
