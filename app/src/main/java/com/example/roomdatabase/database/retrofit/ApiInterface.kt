package com.example.roomdatabase.database.retrofit


import androidx.room.Delete
import com.example.roomdatabase.database.bean.StudentTable

import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {
    @GET("student.json")
    fun fetchAllPosts(): Call<HashMap<String, StudentTable>>

    @PUT("student/{id}.json")
    fun putData(@Body data:StudentTable, @Path("id")id:String): Call<StudentTable>

    @DELETE("student/{id}.json")
    fun deleteData(@Path("id")id: String):Call<StudentTable>


}