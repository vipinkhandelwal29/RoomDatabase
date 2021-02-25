package com.example.roomdatabase.database.retrofit

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://roomdatabase-eb297-default-rtdb.firebaseio.com/"

class ApiClient {
    companion object {
        private var retrofit: Retrofit? = null
        fun getApiClient(): Retrofit {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(100, TimeUnit.SECONDS)
                .connectTimeout(100, TimeUnit.SECONDS)
                .build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit!!
        }
    }
}





























/*
.addInterceptor { chain ->
    val request = chain.request()
    val response = chain.proceed(request)
    if (request.url().pathSegments().contains("student.json")) {
        val parser = JsonParser()
        val el = parser.parse(response.body()?.string())
        val newJsonArray: JsonArray = JsonArray()
        val rootobj = el.asJsonObject()
        val allObjSet: Set<Map.Entry<String, JsonElement>> entries = obj.entrySet()

        allObjSet {
            newJsonArray.add(it.value)
        }

        return@addInterceptor response.newBuilder().body(
            ResponseBody.create(
                MediaType.parse("a")
            )
        )
    }
}*/
