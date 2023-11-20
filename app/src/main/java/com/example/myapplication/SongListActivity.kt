package com.example.myapplication

import MusicDatabase
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.nfc.Tag
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SongListActivity : ComponentActivity() {
    private lateinit var songListView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.song_list_activity)
        songListView = findViewById(R.id.songListView)

        val songRetriever = SongRetriever(contentResolver)
        val songList = songRetriever.retrieveSong()

        val layoutManager = LinearLayoutManager(this)
        songListView.layoutManager = layoutManager
        val adapter = SongListAdapter(songList,this)
        songListView.adapter = adapter
    }
}
data class Song(val name: String, val artist: String, val id:Int,val duration:Int, val size:Long, val mimeType:String,val data:String,
                var isCollected:Boolean=false)

//适配器1
class SongListAdapter(private val songList: List<Song>,private val context: Context):RecyclerView.Adapter<SongListAdapter.ViewHolder>(){

    private var isCollected=false
    private val dbHelper=MusicDatabase(context)
    val db=dbHelper.writableDatabase

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val songName:TextView=view.findViewById(R.id.songName)
        val artistName:TextView=view.findViewById(R.id.artistName)

        val likeButton:Button=itemView.findViewById(R.id.MyLike_button)
        init {
            likeButton.setOnClickListener{
               val position=adapterPosition
                if(position!=RecyclerView.NO_POSITION) {
                    val song = songList[position]
                    if (song.isCollected) {
                        likeButton.setBackgroundResource(R.drawable.heart)
                        val values=ContentValues().apply{
                            put(MusicDatabase.COLUMN_NAME,song.name)
                            put(MusicDatabase.COLUMN_ARTIST,song.artist)
                            put(MusicDatabase.COLUMN_ID,song.id)
                            put(MusicDatabase.COLUMN_DURATION,song.duration)
                            put(MusicDatabase.COLUMN_MIMETYPE,song.mimeType)
                            put(MusicDatabase.COLUMN_SIZE,song.size)
                            put(MusicDatabase.COLUMN_DATA,song.data)
                        }
                        val newRowId=db.insert(MusicDatabase.TABLE_NAME,null,values)
                        if(newRowId!=-1L){
                            Log.i("tag","successful")
                        }else{
                            Log.i("ww","wrong")
                        }
                    } else {
                        likeButton.setBackgroundResource(R.drawable.isheart)
//                        val selection="${MusicDatabase.COLUMN_NAME}=?" +
//                                "ANd${MusicDatabase.COLUMN_ARTIST}=?" +
//                                "ANd${MusicDatabase.COLUMN_ID}=?" +
//                                "AND${MusicDatabase.COLUMN_DURATION}=?" +
//                                "AND${MusicDatabase.COLUMN_MIMETYPE}=?" +
//                                "AND${MusicDatabase.COLUMN_SIZE}=?"
//                        val selectionArgs= arrayOf(song.name, song.artist, song.id.toString(), song.duration.toString(), song.mimeType, song.size.toString())
//                        val deleteRows=db.delete(MusicDatabase.TABLE_NAME,selection,selectionArgs)
//                        if(deleteRows>0){
//                            Log.i("tag","successful")
//                        }else{
//                            Log.i("yy","wrong")
//                        }
                    }
                    song.isCollected = !song.isCollected
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.song_list_item_activity,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song=songList[position]
        holder.songName.text=song.name
        holder.artistName.text=song.artist

        if(isCollected){
            holder.likeButton.setBackgroundResource(R.drawable.heart)
        }else{
            holder.likeButton.setBackgroundResource(R.drawable.isheart)
        }
    }

    override fun getItemCount()=songList.size

}

//获取本地歌曲
class SongRetriever(private val contentResolver: ContentResolver){

    fun retrieveSong(): List<Song> {
        val songList= mutableListOf<Song>()
        val songUri=MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val projection= arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATA
        )
        val selection="${MediaStore.Audio.Media.IS_MUSIC}!=0"
        val sortOrder="${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        contentResolver.query(songUri,projection,selection,null,sortOrder)?.use { cursor->
            val idColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val mimeTypeColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val dataColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while(cursor.moveToNext()){
                val id=cursor.getInt(idColumn)
                val title=cursor.getString(titleColumn)
                val artist=cursor.getString(artistColumn)
                val duration=cursor.getInt(durationColumn)
                val size=cursor.getLong(sizeColumn)
                val mimeType=cursor.getString(mimeTypeColumn)
                val data=cursor.getString(dataColumn)
                val song=Song(title,artist,id,duration,size,mimeType,data)

                songList.add(song)
            }
            cursor.close()
        }
        return songList
    }
}