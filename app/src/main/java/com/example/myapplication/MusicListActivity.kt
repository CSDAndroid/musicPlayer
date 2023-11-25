package com.example.myapplication

import MusicDatabase
import MusicListAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MusicListActivity : ComponentActivity() {
    private lateinit var musicListView:RecyclerView
    private lateinit var back1:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_list_activity)
        musicListView=findViewById(R.id.MusicListView)
        back1=findViewById((R.id.back1))

        back1.setOnClickListener{
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val musicList=getMusicList()

        val layoutManager = LinearLayoutManager(this)
        musicListView.layoutManager = layoutManager

        val adapter = MusicListAdapter(musicList,this)
        musicListView.adapter = adapter
    }

    private fun getMusicList(): ArrayList<Music> {
        val musicList=ArrayList<Music>()
        val dbHelper=MusicDatabase(this)
        val db=dbHelper.writableDatabase

        val cursor=db.query(
            MusicDatabase.TABLE_NAME,
            arrayOf(MusicDatabase.COLUMN_NAME,MusicDatabase.COLUMN_ID,MusicDatabase.COLUMN_ARTIST,MusicDatabase.COLUMN_DURATION,MusicDatabase.COLUMN_DATA,MusicDatabase.COLUMN_MIMETYPE,MusicDatabase.COLUMN_SIZE,MusicDatabase.COLUMN_ALBUM_ID),
            null,
            null,
            null,
            null,
            null,
            null)

        while(cursor.moveToNext()){
            val id=cursor.getInt(cursor.getColumnIndexOrThrow(MusicDatabase.COLUMN_ID))
            val name=cursor.getString(cursor.getColumnIndexOrThrow(MusicDatabase.COLUMN_NAME))
            val artist=cursor.getString(cursor.getColumnIndexOrThrow(MusicDatabase.COLUMN_ARTIST))
            val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MusicDatabase.COLUMN_DURATION))
            val data = cursor.getString(cursor.getColumnIndexOrThrow(MusicDatabase.COLUMN_DATA))
            val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MusicDatabase.COLUMN_MIMETYPE))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(MusicDatabase.COLUMN_SIZE))
            val albumId=cursor.getLong(cursor.getColumnIndexOrThrow(MusicDatabase.COLUMN_ALBUM_ID))

            if (name != null && artist != null && data != null && mimeType != null) {
                val music = Music(name, artist, id, data, duration, size, mimeType,albumId)
                musicList.add(music)
            }
        }

        cursor.close()
        return musicList
    }
}

data class Music(
    val name: String, val artist: String, val id: Int, val data: String, val duration: Int, val size: Long, val mimeType: String,
    val albumId:Long,var isCollected:Boolean=true)