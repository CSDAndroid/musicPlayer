package com.example.myapplication.ui.theme

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//定义一个接口
interface Http {
    @GET("/playlist/track/all?id=24381616&limit=10&offset=10")
    suspend fun getMusic(): Response<JsonObject>

    @GET("song/url/")
    suspend fun getMusicUrl(@Query("id") id: Int): Response<JsonObject>

    @GET("/search")
    suspend fun getMusicKey(@Query("keywords") keywords: String): Response<JsonObject>
}