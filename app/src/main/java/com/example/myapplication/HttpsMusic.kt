package com.example.myapplication

import HttpListAdapter
import Song
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

class HttpsMusic : ComponentActivity() {
    private lateinit var httpListView:RecyclerView
    private lateinit var back2:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.https_music)
        httpListView=findViewById(R.id.HttpMusicListView)
        back2=findViewById(R.id.back2)

        back2.setOnClickListener{
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val musicList=main()

        val layoutManager = LinearLayoutManager(this)
        httpListView.layoutManager = layoutManager
        val adapter = HttpListAdapter(musicList,this)
        httpListView.adapter = adapter
    }

    private fun main():ArrayList<Song>{
        val musicList= ArrayList<Song>()
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
            val response= service.getMusic()
            val body=response.body().toString()
            Log.d("MainActivity", "onCreate:$body")

            val jsonObject=JSONObject(body)
            val musicsArray=jsonObject.getJSONArray("songs")
            for(i in 0 until musicsArray.length()){
                val musicObject=musicsArray.getJSONObject(i)
                val name =musicObject.getString("name")
                Log.d("TUU","name:$name")
                val artistsArray=musicObject.getJSONArray("ar")
                var artist:String?=null
                if(artistsArray.length()>0){
                    val artistObject=artistsArray.getJSONObject(0)
                    artist=artistObject.getString("name")
                    Log.d("TRE","artistNames:$artist")
                }
                val id=musicObject.getInt("id")
                val path="htt[://8.222.172.78:3000/songs/id"
                Log.d("TAD","path:$path")
                val durationInMillisecond=musicObject.getInt("dt")
                val duration=durationInMillisecond/1000
                Log.d("TGV","duration:$duration")
                val music=Song(name,artist!!,id,duration,0,"",path,0,false)
                musicList.add(music)
            }
        }
        return musicList
    }

    interface Test{
        @GET("/playlist/track/all?id=24381616&limit=20&offset=10")
        suspend fun getMusic(): Response<JsonObject>
    }
}