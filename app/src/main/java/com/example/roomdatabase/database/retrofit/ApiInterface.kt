package com.example.roomdatabase.database.retrofit


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("student.json")
    fun fetchAllPosts(): Call<ResponseBody>
}