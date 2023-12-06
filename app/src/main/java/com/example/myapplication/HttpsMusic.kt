package com.example.myapplication

import Music
import android.os.Bundle
import android.os.Message
import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

class HttpsMusic : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.https_music)

        val musicList=main()
    }

    private fun main():ArrayList<Music>{
        val musicList=ArrayList<Music>()

        val loggingInterceptor=HttpLoggingInterceptor().apply {
            level=HttpLoggingInterceptor.Level.BODY
        }

        val client=OkHttpClient.Builder()
            .connectTimeout(3000L,TimeUnit.MILLISECONDS)
            .writeTimeout(10,TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit=Retrofit.Builder()
            .baseUrl("https://www.kugou.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service=retrofit.create(Test::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val musicResponse=service.getMusic()
            musicList.add(musicResponse.music)
        }
        return musicList
    }

    interface Test{
        @GET("music")
        suspend fun getMusic():ShortMusicResponse
    }
}

data class ShortMusicResponse(val code:Int,val message: Message,val music: Music)
