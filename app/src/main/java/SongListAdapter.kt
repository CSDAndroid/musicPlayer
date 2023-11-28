import android.content.ContentValues
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class SongListAdapter(private val songList: List<Song>, context: Context): RecyclerView.Adapter<SongListAdapter.ViewHolder>(){

    private val dbHelper=MusicDatabase(context)
    val db=dbHelper.writableDatabase
    private val sharedPreferences=context.getSharedPreferences("isCollected", Context.MODE_PRIVATE)

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val songName: TextView =view.findViewById(R.id.songName)
        val artistName: TextView =view.findViewById(R.id.artistName)
        val playButton: Button =view.findViewById(R.id.play_button)
        val likeButton: Button =itemView.findViewById(R.id.MyLike_button)
        init {
            likeButton.setOnClickListener{
                val position = bindingAdapterPosition
                if(position!= RecyclerView.NO_POSITION) {
                    val song = songList[position]
                    val isCollected=sharedPreferences.getBoolean("isCollected_${song.id}",false)
                    if (!isCollected) {
                        likeButton.setBackgroundResource(R.drawable.heart)
                        val editor=sharedPreferences.edit()
                        editor.putBoolean("isCollected_${song.id}",true)
                        editor.apply()
                        val values= ContentValues().apply{
                            put(MusicDatabase.COLUMN_NAME,song.name)
                            put(MusicDatabase.COLUMN_ARTIST,song.artist)
                            put(MusicDatabase.COLUMN_ID,song.id)
                            put(MusicDatabase.COLUMN_DURATION,song.duration)
                            put(MusicDatabase.COLUMN_MIMETYPE,song.mimeType)
                            put(MusicDatabase.COLUMN_SIZE,song.size)
                            put(MusicDatabase.COLUMN_DATA,song.data)
                            put(MusicDatabase.COLUMN_ALBUM_ID,song.albumId)
                        }
                        db.insert(MusicDatabase.TABLE_NAME,null,values)
                    } else {
                        likeButton.setBackgroundResource(R.drawable.isheart)
                        val editor=sharedPreferences.edit()
                        editor.putBoolean("isCollected_${song.id}",false)
                        editor.apply()
                        val selection = "${MusicDatabase.COLUMN_NAME}=? AND ${MusicDatabase.COLUMN_ARTIST}=? AND ${MusicDatabase.COLUMN_ID}=? AND ${MusicDatabase.COLUMN_DURATION}=? AND ${MusicDatabase.COLUMN_MIMETYPE}=? AND ${MusicDatabase.COLUMN_SIZE}=? AND ${MusicDatabase.COLUMN_DATA}=? AND ${MusicDatabase.COLUMN_ALBUM_ID}=?"
                        val selectionArgs= arrayOf(song.name, song.artist, song.id.toString(), song.duration.toString(), song.mimeType, song.size.toString(), song.data,song.albumId.toString())
                        db.delete(MusicDatabase.TABLE_NAME,selection,selectionArgs)
                    }
                    song.isCollected = !song.isCollected
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.song_list_item_activity,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song=songList[position]
        holder.songName.text=song.name
        holder.artistName.text=song.artist
        val isCollected=sharedPreferences.getBoolean("isCollected_${song.id}",false)
        if(isCollected){
            holder.likeButton.setBackgroundResource(R.drawable.heart)
        }else{
            holder.likeButton.setBackgroundResource(R.drawable.isheart)
        }
    }

    override fun getItemCount()=songList.size
}