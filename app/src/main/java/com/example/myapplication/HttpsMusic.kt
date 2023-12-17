package com.example.myapplication

import HttpListAdapter
import Song
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class HttpsMusic : ComponentActivity() {
    private lateinit var httpListView:RecyclerView
    private lateinit var back2:Button
    private lateinit var search2:Button
    private lateinit var musicSearch:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.https_music)
        httpListView=findViewById(R.id.HttpMusicListView)
        back2=findViewById(R.id.back2)

        back2.setOnClickListener{
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val musicList = getHttpMusic()
                // 在主线程中更新 UI
                withContext(Dispatchers.Main) {
                    val layoutManager = LinearLayoutManager(this@HttpsMusic)
                    httpListView.layoutManager = layoutManager
                    val adapter = HttpListAdapter(musicList,this@HttpsMusic)
                    httpListView.adapter = adapter
                }
            } catch (e: Exception) {
                //其他东西
            }
        }

        search2=findViewById(R.id.search2)
        search2.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val musicList=getHttpMusic1()
                    withContext(Dispatchers.Main) {
                        val layoutManager = LinearLayoutManager(this@HttpsMusic)
                        httpListView.layoutManager = layoutManager
                        val adapter = HttpListAdapter(musicList, this@HttpsMusic)
                        httpListView.adapter = adapter
                    }
                }catch (e:Exception){
                    //处理异常
                }
            }
        }
    }

    private suspend fun getHttpMusic1():ArrayList<Song>{
        val musicList= ArrayList<Song>()
        //拦截器
        val loggingInterceptor=HttpLoggingInterceptor().apply {
            level=HttpLoggingInterceptor.Level.BODY
        }

        //创建客户端实例
        val client=OkHttpClient.Builder()
            .connectTimeout(3000L,TimeUnit.MILLISECONDS)
            .writeTimeout(10,TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build()

        //创建retrofit实例
        val retrofit=Retrofit.Builder()
            .baseUrl("http://8.222.172.78:3000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())//Gson转换器
            .build()

        val service1=retrofit.create(Http::class.java)

        musicSearch=findViewById(R.id.MusicSearch)
        val keywords=musicSearch.text.toString()
        try {
            val response2=service1.getMusicKey(keywords)
            val body2=response2.body().toString()
            Log.d("HttpActivity","body:$body2")

            val jsonObject = JSONObject(body2)
            val songs = jsonObject.getJSONObject("result").getJSONArray("songs")

            for (i in 0 until songs.length()) {
                val song = songs.getJSONObject(i)
                val songName = song.getString("name")

                val songId = song.getInt("id")
                val response1=service1.getMusicUrl(songId)
                val body1=response1.body().toString()
                val jsonObject1=JSONObject(body1)
                val urlArray=jsonObject1.getJSONArray("data")
                val urlObject=urlArray.getJSONObject(0)
                val url=urlObject.getString("url")

                val durationInMilliseconds = song.getInt("duration")
                val duration=durationInMilliseconds/1000

                val artists = song.getJSONArray("artists")
                var artistNames :String?= null
                if(artists.length() > 0){
                    val artistsObject=artists.getJSONObject(0)
                    artistNames=artistsObject.getString("name")
                }

                if(artistNames!=null){
                    val music=Song(songName,artistNames,songId,duration,0,"",url,0,false)
                    musicList.add(music)
                }
            }

        }catch (e: Exception) {
            Log.e("HttpActivity", "Error fetching music", e)
        }
        return musicList
    }

    private suspend fun getHttpMusic():ArrayList<Song>{
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
            .baseUrl("http://8.222.172.78:3000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //创建一个retrofit服务接口
        val service=retrofit.create(Http::class.java)

        try {
            val response= service.getMusic()
            val body=response.body().toString()
            Log.d("MainActivity", "onCreate:$body")

            val jsonObject= JSONObject(body)
            val musicsArray=jsonObject.getJSONArray("songs")
            for(i in 0 until musicsArray.length()) {
                val musicObject = musicsArray.getJSONObject(i)

                val name = musicObject.getString("name")
                Log.d("TUU", "name:$name")


                val artistsArray = musicObject.getJSONArray("ar")
                var artist:String?=null
                if (artistsArray.length() > 0) {
                    val artistObject = artistsArray.getJSONObject(0)
                    artist = artistObject.getString("name")
                }
                Log.d("TRE", "artistNames:$artist")


                val id=musicObject.getInt("id")
                Log.d("TQQ","id:$id")

                val response1=service.getMusicUrl(id)
                val body1=response1.body().toString()
                val jsonObject1=JSONObject(body1)
                val urlArray=jsonObject1.getJSONArray("data")
                val urlObject=urlArray.getJSONObject(0)
                val url=urlObject.getString("url")
                Log.d("TNM","url:$url")


                val durationInMilliseconds = musicObject.getInt("dt")
                val duration = durationInMilliseconds / 1000
                Log.d("TGV", "duration:$duration")

                if(artist!=null){
                    val music=Song(name,artist,id,duration,0,"",url,0,false)
                    musicList.add(music)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error fetching music", e)
        }
        return musicList
    }

    //定义一个接口
    interface Http{
        @GET("/playlist/track/all?id=24381616&limit=10&offset=10")
        suspend fun getMusic(): Response<JsonObject>
        @GET("song/url/")
        suspend fun getMusicUrl(@Query("id") id: Int):Response<JsonObject>
        @GET("/search")
        suspend fun getMusicKey(@Query("keywords") keywords:String):Response<JsonObject>
    }
}