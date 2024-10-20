package com.example.myapplication

import Song
import SongListAdapter
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
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class HttpsMusic : ComponentActivity() {
    private lateinit var httpListView: RecyclerView
    private lateinit var back2: Button
    private lateinit var search2: Button
    private lateinit var musicSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.https_music)
        httpListView = findViewById(R.id.HttpMusicListView)
        back2 = findViewById(R.id.back2)

        back2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val musicList = getHttpMusic()
                // 在主线程中更新 UI
                withContext(Dispatchers.Main) {
                    val layoutManager = LinearLayoutManager(this@HttpsMusic)
                    httpListView.layoutManager = layoutManager
                    val adapter = SongListAdapter(musicList, this@HttpsMusic)
                    httpListView.adapter = adapter
                }
            } catch (e: Exception) {
                //其他东西
            }
        }

        search2 = findViewById(R.id.search2)
        search2.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val musicList = getHttpMusic1()
                    withContext(Dispatchers.Main) {
                        val layoutManager = LinearLayoutManager(this@HttpsMusic)
                        httpListView.layoutManager = layoutManager
                        val adapter = SongListAdapter(musicList, this@HttpsMusic)
                        httpListView.adapter = adapter
                    }
                } catch (e: Exception) {
                    //处理异常
                }
            }
        }
    }

    private suspend fun getHttpMusic1(): ArrayList<Song> {
        val musicList = ArrayList<Song>()
        val address = "http://8.222.172.78:3000/"
        val service1 = HttpUtil.sendHttp(address, Http::class.java)

        musicSearch = findViewById(R.id.MusicSearch)
        val keywords = musicSearch.text.toString()
        try {
            val response2 = service1.getMusicKey(keywords)
            val body2 = response2.body().toString()

            val jsonObject = JSONObject(body2)
            val songs = jsonObject.getJSONObject("result").getJSONArray("songs")

            for (i in 0 until songs.length()) {
                val song = songs.getJSONObject(i)
                val songName = song.getString("name")

                val songId = song.getInt("id")
                val response1 = service1.getMusicUrl(songId)
                val body1 = response1.body().toString()
                val jsonObject1 = JSONObject(body1)
                val urlArray = jsonObject1.getJSONArray("data")
                val urlObject = urlArray.getJSONObject(0)
                val url = urlObject.getString("url")

                val durationInMilliseconds = song.getInt("duration")
                val duration = durationInMilliseconds / 1000

                val artists = song.getJSONArray("artists")
                var artistNames: String? = null
                if (artists.length() > 0) {
                    val artistsObject = artists.getJSONObject(0)
                    artistNames = artistsObject.getString("name")
                }
                if (artistNames != null) {
                    val music = Song(songName, artistNames, songId, duration, 0, "", url, 0, false)
                    musicList.add(music)
                }
            }
        } catch (e: Exception) {
            Log.e("HttpActivity", "Error fetching music", e)
        }
        return musicList
    }

    private suspend fun getHttpMusic(): ArrayList<Song> {
        val musicList = ArrayList<Song>()
        val address = "http://8.222.172.78:3000/"
        val service = HttpUtil.sendHttp(address, Http::class.java)

        try {
            val response = service.getMusic()
            val body = response.body().toString()

            val jsonObject = JSONObject(body)
            val musicsArray = jsonObject.getJSONArray("songs")
            for (i in 0 until musicsArray.length()) {
                val musicObject = musicsArray.getJSONObject(i)
                val name = musicObject.getString("name")
                Log.d("TUU", "name:$name")

                val artistsArray = musicObject.getJSONArray("ar")
                var artist: String? = null
                if (artistsArray.length() > 0) {
                    val artistObject = artistsArray.getJSONObject(0)
                    artist = artistObject.getString("name")
                }
                Log.d("TRE", "artistNames:$artist")

                val durationInMilliseconds = musicObject.getInt("dt")
                val duration = durationInMilliseconds / 1000
                Log.d("TGV", "duration:$duration")

                val id = musicObject.getInt("id")
                Log.d("TQQ", "id:$id")

                val response1 = service.getMusicUrl(id)
                val body1 = response1.body().toString()
                val jsonObject1 = JSONObject(body1)
                val urlArray = jsonObject1.getJSONArray("data")
                val urlObject = urlArray.getJSONObject(0)
                val url = urlObject.getString("url")
                Log.d("TNM", "url:$url")

                if (artist != null) {
                    val music = Song(name, artist, id, duration, 0, "", url, 0, false)
                    musicList.add(music)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error fetching music", e)
        }
        return musicList
    }

    //定义一个接口
    interface Http {
        @GET("/playlist/track/all?id=24381616&limit=10&offset=10")
        suspend fun getMusic(): Response<JsonObject>

        @GET("song/url/")
        suspend fun getMusicUrl(@Query("id") id: Int): Response<JsonObject>

        @GET("/search")
        suspend fun getMusicKey(@Query("keywords") keywords: String): Response<JsonObject>
    }
}

//定义一个单例类,用于重复使用
object HttpUtil {
    //这是一个泛型函数，接受一个地址 address 和一个服务类 serviceClass，并返回一个泛型类型 T 的结果。
    fun <T : Any> sendHttp(address: String, serviceClass: Class<T>): T {

        //创建客户端实例,用于配置和定制 HTTP 请求的行为，例如超时时间、拦截器等。
        val client = OkHttpClient.Builder()
            .connectTimeout(300L, TimeUnit.MILLISECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        //创建retrofit实例,可以用来创建 API 接口的实例，发起网络请求等操作
        val retrofit = Retrofit.Builder()
            .baseUrl(address)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())//json转换器
            .build()

        //使用 Retrofit 实例创建一个特定类型的服务接口实例，该接口由参数 serviceClass 指定，并将其返回。
        return retrofit.create(serviceClass)
    }
}