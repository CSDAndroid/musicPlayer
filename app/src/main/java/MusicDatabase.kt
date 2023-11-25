import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class MusicDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MusicListDatabaseHelper.db"
        const val TABLE_NAME = "songs"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_ARTIST = "artist"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_DATA = "data"
        const val COLUMN_MIMETYPE = "mimeType"
        const val COLUMN_SIZE = "size"
        const val COLUMN_ALBUM_ID="albumId"
        const val COLUMN_ALBUM_ART_BITMAP="albumArtBitmap"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_NAME TEXT, $COLUMN_ARTIST TEXT, $COLUMN_DURATION INTEGER,$COLUMN_DATA TEXT,$COLUMN_MIMETYPE TEXT,$COLUMN_SIZE INTEGER,$COLUMN_ALBUM_ID INTEGER,$COLUMN_ALBUM_ART_BITMAP BLOB)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Do nothing for now
    }
}
