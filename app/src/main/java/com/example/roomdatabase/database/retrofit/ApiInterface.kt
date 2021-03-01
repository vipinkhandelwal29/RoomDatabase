package com.example.roomdatabase.database.retrofit


import androidx.room.Delete
import com.example.roomdatabase.database.bean.StudentTable
import com.example.roomdatabase.database.fcm.FCMPayLoad
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {
    @GET("student.json")
    fun fetchAllPosts(): Call<HashMap<String, StudentTable>>

    @PUT("student/{id}.json")
    fun putData(@Body data: StudentTable, @Path("id") id: String): Call<StudentTable>

    @DELETE("student/{id}.json")
    fun deleteData(@Path("id") id: String): Call<StudentTable>

    @POST("send")
    fun send(@Body data: FCMPayLoad): Call<ResponseBody>
}