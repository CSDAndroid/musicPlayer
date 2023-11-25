import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.myapplication.Song

class SongRetriever(private val contentResolver: ContentResolver){

    fun retrieveSong(): List<Song> {
        val songList= mutableListOf<Song>()
        val songUri= MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
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

                val song= Song(title,artist,id,duration,size,mimeType,data,albumId,albumArtBitmap)

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