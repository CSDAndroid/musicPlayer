import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MusicPlayer2
import com.example.myapplication.R

class MusicListAdapter(val musicList: List<Song>, context: Context) :
    RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {

    private val dbHelper = MusicDatabase(context)
    val db = dbHelper.writableDatabase
    private val sharedPreferences =
        context.getSharedPreferences("isCollected", Context.MODE_PRIVATE)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val musicName: TextView = view.findViewById(R.id.musicName)
        val artistName1: TextView = view.findViewById(R.id.artistName1)

        val playButton1: Button = view.findViewById(R.id.play_button1)

        init {
            playButton1.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    playButton1.setBackgroundResource(R.drawable.ic_pause)
                    SongListAdapter.MusicData.currentSongList = musicList
                    SongListAdapter.MusicData.currentPosition = position
                    val intent = Intent(itemView.context, MusicPlayer2::class.java)
                    itemView.context.startActivities(arrayOf(intent))
                    playButton1.setBackgroundResource(R.drawable.ic_play)
                }
            }
        }

        val likeButton1: Button = itemView.findViewById(R.id.MyLike_button1)

        init {
            likeButton1.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val music = musicList[position]
                    val isCollected = sharedPreferences.getBoolean("isCollected_${music.id}", false)
                    if (!isCollected) {
                        likeButton1.setBackgroundResource(R.drawable.heart)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isCollected_${music.id}", true)
                        editor.apply()
                        val values = ContentValues().apply {
                            put(MusicDatabase.COLUMN_NAME, music.name)
                            put(MusicDatabase.COLUMN_ARTIST, music.artist)
                            put(MusicDatabase.COLUMN_ID, music.id)
                            put(MusicDatabase.COLUMN_DURATION, music.duration)
                            put(MusicDatabase.COLUMN_MIMETYPE, music.mimeType)
                            put(MusicDatabase.COLUMN_SIZE, music.size)
                            put(MusicDatabase.COLUMN_DATA, music.data)
                            put(MusicDatabase.COLUMN_ALBUM_ID, music.albumId)
                        }
                        db.insert(MusicDatabase.TABLE_NAME, null, values)
                    } else {
                        likeButton1.setBackgroundResource(R.drawable.isheart)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isCollected_${music.id}", false)
                        editor.apply()
                        val selection =
                            "${MusicDatabase.COLUMN_NAME}=? AND ${MusicDatabase.COLUMN_ARTIST}=? AND ${MusicDatabase.COLUMN_ID}=? AND ${MusicDatabase.COLUMN_DURATION}=? AND ${MusicDatabase.COLUMN_MIMETYPE}=? AND ${MusicDatabase.COLUMN_SIZE}=? AND ${MusicDatabase.COLUMN_DATA}=? AND ${MusicDatabase.COLUMN_ALBUM_ID}=?"
                        val selectionArgs = arrayOf(
                            music.name,
                            music.artist,
                            music.id.toString(),
                            music.duration.toString(),
                            music.mimeType,
                            music.size.toString(),
                            music.data,
                            music.albumId.toString()
                        )
                        db.delete(MusicDatabase.TABLE_NAME, selection, selectionArgs)
                    }
                    music.isCollected = !music.isCollected
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_list_item_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = musicList[position]
        holder.musicName.text = music.name
        holder.artistName1.text = music.artist
        holder.playButton1.setBackgroundResource(R.drawable.ic_play)

        val isCollected = sharedPreferences.getBoolean("isCollected_${music.id}", false)
        if (isCollected) {
            holder.likeButton1.setBackgroundResource(R.drawable.heart)
        } else {
            holder.likeButton1.setBackgroundResource(R.drawable.isheart)
        }
    }

    override fun getItemCount() = musicList.size
}
