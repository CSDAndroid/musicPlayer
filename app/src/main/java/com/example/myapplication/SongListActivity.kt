package com.example.myapplication

import SongListAdapter
import SongRetriever
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SongListActivity : ComponentActivity() {
    private lateinit var songListView: RecyclerView
    private lateinit var back: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.song_list_activity)
        songListView = findViewById(R.id.songListView)
        back = findViewById(R.id.back)

        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val songRetriever = SongRetriever(contentResolver)
        val songList = songRetriever.retrieveSong()

        val layoutManager = LinearLayoutManager(this)
        songListView.layoutManager = layoutManager
        val adapter = SongListAdapter(songList, this)
        songListView.adapter = adapter

//        val musicPlayer=findViewById<MusicPlayer>(R.id.musicPlayer)
//        val position=getPosition(null, songList)
//        if (position != null) {
//            musicPlayer.getSongList(songList,position)
//        }
//        musicPlayer.init()
    }

//    companion object {
//        fun getPosition(position: Int?, songList:List<Song>): Int? {
//            return position
//        }
}