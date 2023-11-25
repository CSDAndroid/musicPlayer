package com.example.myapplication

import MusicDatabase
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream

class SongListActivity : ComponentActivity() {
    private lateinit var songListView: RecyclerView
    private lateinit var back:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.song_list_activity)
        songListView = findViewById(R.id.songListView)
        back=findViewById(R.id.back)

        back.setOnClickListener{
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val songRetriever = SongRetriever(contentResolver)
        val songList = songRetriever.retrieveSong()

        val layoutManager = LinearLayoutManager(this)
        songListView.layoutManager = layoutManager
        val adapter = SongListAdapter(songList,this)
        songListView.adapter = adapter
    }
}
//歌曲的数据类
data class Song(val name: String, val artist: String, val id:Int, val duration:Int, val size:Long, val mimeType:String, val data:String,
                val albumId:Long, var albumArtBitmap: Bitmap?, var isCollected:Boolean=false)

//歌曲适配器,收藏的点击事件和播放的点击事件
class SongListAdapter(private val songList: List<Song>, context: Context):RecyclerView.Adapter<SongListAdapter.ViewHolder>(){

    private val dbHelper=MusicDatabase(context)
    val db=dbHelper.writableDatabase
    private val sharedPreferences=context.getSharedPreferences("isCollected",Context.MODE_PRIVATE)

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val songName:TextView=view.findViewById(R.id.songName)
        val artistName:TextView=view.findViewById(R.id.artistName)
        val picture:ImageView=view.findViewById(R.id.picture)

        val likeButton:Button=itemView.findViewById(R.id.MyLike_button)
        init {
            likeButton.setOnClickListener{
                val position = bindingAdapterPosition
                if(position!=RecyclerView.NO_POSITION) {
                    val song = songList[position]
                    val isCollected=sharedPreferences.getBoolean("isCollected_${song.id}",false)
                    if (!isCollected) {
                        likeButton.setBackgroundResource(R.drawable.heart)
                        val editor=sharedPreferences.edit()
                        editor.putBoolean("isCollected_${song.id}",true)
                        editor.apply()

                        val values=ContentValues().apply{
                            put(MusicDatabase.COLUMN_NAME,song.name)
                            put(MusicDatabase.COLUMN_ARTIST,song.artist)
                            put(MusicDatabase.COLUMN_ID,song.id)
                            put(MusicDatabase.COLUMN_DURATION,song.duration)
                            put(MusicDatabase.COLUMN_MIMETYPE,song.mimeType)
                            put(MusicDatabase.COLUMN_SIZE,song.size)
                            put(MusicDatabase.COLUMN_DATA,song.data)
                            put(MusicDatabase.COLUMN_ALBUM_ID,song.albumId)
                            put(MusicDatabase.COLUMN_ALBUM_ART_BITMAP,
                                song.albumArtBitmap?.let { it1 -> getBitmapAsByteArray(it1) })
                        }
                        db.insert(MusicDatabase.TABLE_NAME,null,values)
                    } else {
                        likeButton.setBackgroundResource(R.drawable.isheart)
                        val editor=sharedPreferences.edit()
                        editor.putBoolean("isCollected_${song.id}",false)
                        editor.apply()

                        val selection = "${MusicDatabase.COLUMN_NAME}=? AND ${MusicDatabase.COLUMN_ARTIST}=? AND ${MusicDatabase.COLUMN_ID}=? AND ${MusicDatabase.COLUMN_DURATION}=? AND ${MusicDatabase.COLUMN_MIMETYPE}=? AND ${MusicDatabase.COLUMN_SIZE}=? AND ${MusicDatabase.COLUMN_DATA}=? AND ${MusicDatabase.COLUMN_ALBUM_ID}=? AND ${MusicDatabase.COLUMN_ALBUM_ART_BITMAP}=?"
                        val selectionArgs= arrayOf(song.name, song.artist, song.id.toString(), song.duration.toString(), song.mimeType, song.size.toString(), song.data,song.albumId.toString(),song.albumArtBitmap.toString())
                        db.delete(MusicDatabase.TABLE_NAME,selection,selectionArgs)
                    }
                    song.isCollected = !song.isCollected
                }
            }
        }
        private fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
            return stream.toByteArray()
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
        holder.picture.setImageBitmap(song.albumArtBitmap)

        val isCollected=sharedPreferences.getBoolean("isCollected_${song.id}",false)
        if(isCollected){
            holder.likeButton.setBackgroundResource(R.drawable.heart)
        }else{
            holder.likeButton.setBackgroundResource(R.drawable.isheart)
        }
    }

    override fun getItemCount()=songList.size
}

//获取本地歌曲的各种信息
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
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
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
            val albumIdColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val dataColumn=cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while(cursor.moveToNext()){
                val id=cursor.getInt(idColumn)
                val title=cursor.getString(titleColumn)
                val artist=cursor.getString(artistColumn)
                val duration=cursor.getInt(durationColumn)
                val size=cursor.getLong(sizeColumn)
                val mimeType=cursor.getString(mimeTypeColumn)
                val data=cursor.getString(dataColumn)
                val albumId=cursor.getLong(albumIdColumn)

                val albumArtBitmap=getAlbumArtBitmap(contentResolver,albumId)

                val song=Song(title,artist,id,duration,size,mimeType,data,albumId,albumArtBitmap)

                songList.add(song)
            }
            cursor.close()
        }
        return songList
    }
//获取歌曲图片资源
    private fun getAlbumArtBitmap(contentResolver: ContentResolver, albumId: Long): Bitmap? {
        val albumArtUri = Uri.parse("content://media/external/audio/albumart")
        val uri = ContentUris.withAppendedId(albumArtUri, albumId)
        var bitmap: Bitmap? = null
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            if (parcelFileDescriptor != null) {
                val fileDescriptor = parcelFileDescriptor.fileDescriptor
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor.close()
                Log.i("tag3","successful")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

}