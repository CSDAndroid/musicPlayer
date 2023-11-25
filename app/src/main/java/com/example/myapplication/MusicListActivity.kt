package com.example.myapplication

import MusicDatabase
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

class MusicListAdapter(private val musicList: List<Music>, context: Context):RecyclerView.Adapter<MusicListAdapter.ViewHolder>(){

    private val dbHelper=MusicDatabase(context)
    val db=dbHelper.writableDatabase
    private val sharedPreferences=context.getSharedPreferences("isCollected",Context.MODE_PRIVATE)

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val musicName: TextView =view.findViewById(R.id.musicName)
        val artistName1: TextView =view.findViewById(R.id.artistName1)
        val picture1:ImageView=view.findViewById(R.id.picture1)

        val likeButton1: Button =itemView.findViewById(R.id.MyLike_button1)
        init {
            likeButton1.setOnClickListener{
                val position = bindingAdapterPosition
                if(position!=RecyclerView.NO_POSITION) {
                    val music = musicList[position]
                    val isCollected=sharedPreferences.getBoolean("isCollected_${music.id}",false)
                    if (!isCollected) {
                        likeButton1.setBackgroundResource(R.drawable.heart)
                        val editor=sharedPreferences.edit()
                        editor.putBoolean("isCollected_${music.id}",true)
                        editor.apply()

                        val values= ContentValues().apply{
                            put(MusicDatabase.COLUMN_NAME,music.name)
                            put(MusicDatabase.COLUMN_ARTIST,music.artist)
                            put(MusicDatabase.COLUMN_ID,music.id)
                            put(MusicDatabase.COLUMN_DURATION,music.duration)
                            put(MusicDatabase.COLUMN_MIMETYPE,music.mimeType)
                            put(MusicDatabase.COLUMN_SIZE,music.size)
                            put(MusicDatabase.COLUMN_DATA,music.data)
                            put(MusicDatabase.COLUMN_ALBUM_ID,music.albumId)
                        }
                        db.insert(MusicDatabase.TABLE_NAME,null,values)
                    } else {
                        likeButton1.setBackgroundResource(R.drawable.isheart)
                        val editor=sharedPreferences.edit()
                        editor.putBoolean("isCollected_${music.id}",false)
                        editor.apply()

                        val selection = "${MusicDatabase.COLUMN_NAME}=? AND ${MusicDatabase.COLUMN_ARTIST}=? AND ${MusicDatabase.COLUMN_ID}=? AND ${MusicDatabase.COLUMN_DURATION}=? AND ${MusicDatabase.COLUMN_MIMETYPE}=? AND ${MusicDatabase.COLUMN_SIZE}=? AND ${MusicDatabase.COLUMN_DATA}=? AND ${MusicDatabase.COLUMN_ALBUM_ID}=?"
                        val selectionArgs= arrayOf(music.name, music.artist, music.id.toString(), music.duration.toString(), music.mimeType, music.size.toString(), music.data,music.albumId.toString())
                        db.delete(MusicDatabase.TABLE_NAME,selection,selectionArgs)
                    }
                    music.isCollected = !music.isCollected
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.music_list_item_activity,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music=musicList[position]
        holder.musicName.text=music.name
        holder.artistName1.text=music.artist

        val isCollected=sharedPreferences.getBoolean("isCollected_${music.id}",false)
        if(isCollected){
            holder.likeButton1.setBackgroundResource(R.drawable.heart)
        }else{
            holder.likeButton1.setBackgroundResource(R.drawable.isheart)
        }
    }

    override fun getItemCount()=musicList.size
}
