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
import com.example.myapplication.ui.theme.Http
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

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
}