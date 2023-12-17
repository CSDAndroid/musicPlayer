package com.example.myapplication

import MusicDatabase
import MusicListAdapter
import Song
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

    private fun getMusicList(): ArrayList<Song> {
        val musicList=ArrayList<Song>()
        val dbHelper=MusicDatabase(this)
        val db=dbHelper.writableDatabase

        //使用数据库的query方法查询数据库中的音乐信息
        val cursor=db.query(
            MusicDatabase.TABLE_NAME,
            arrayOf(MusicDatabase.COLUMN_NAME,MusicDatabase.COLUMN_ID,MusicDatabase.COLUMN_ARTIST,MusicDatabase.COLUMN_DURATION,MusicDatabase.COLUMN_DATA,MusicDatabase.COLUMN_MIMETYPE,MusicDatabase.COLUMN_SIZE,MusicDatabase.COLUMN_ALBUM_ID),
            null,
            null,
            null,
            null,
            null,
            null)

        //通过遍历游标(cursor)的方式，将查询到的音乐信息提取出来，并封装成Song对象，添加到musicList中
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
                val song = Song(name, artist, id, duration, size, mimeType, data, albumId)
                musicList.add(song)
            }
        }

        cursor.close()
        return musicList
    }
}