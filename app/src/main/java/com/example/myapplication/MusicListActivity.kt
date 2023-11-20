package com.example.myapplication

import MusicDatabase
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MusicListActivity : ComponentActivity() {
    private lateinit var musicListView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_list_activity)
        musicListView=findViewById(R.id.MusicListView)

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
            null,
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

            if (name != null && artist != null && data != null && mimeType != null) {
                val music = Music(name, artist, id, data, duration, size, mimeType)
                musicList.add(music)
            }
        }

        cursor.close()
        return musicList
    }
}

data class Music(
    val name: String, val artist: String, val id: Int, val data: String, val duration: Int, val size: Long, val mimeType: String,
    var isCollected:Boolean=false)

class MusicListAdapter(private val musicList: List<Music>,private val context: Context):RecyclerView.Adapter<MusicListAdapter.ViewHolder>(){

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val musicName: TextView =view.findViewById(R.id.musicName)
        val artistName1: TextView =view.findViewById(R.id.artistName1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.music_list_item_activity,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music=musicList[position]
        holder.musicName.text=music.name
        holder.artistName1.text=music.artist
    }

    override fun getItemCount()=musicList.size
}
