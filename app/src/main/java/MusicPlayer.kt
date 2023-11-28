
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.example.myapplication.MusicPlayerService
import com.example.myapplication.R

class MusicPlayer(context: Context,attrs:AttributeSet):RelativeLayout(context,attrs){
    private var sb:SeekBar?=null
    private var tv_progress:Int?=null
    private var tv_total:Int?=null
    private var artist:String?=null
    private var name:String?=null
    private var play:Button?=null
    private var prev:Button?=null
    private var next:Button?=null
    private var intent:Intent?=null
    private var list:List<Song>?=null
    private var temp:Int?=null
    private var musicControl:MusicPlayerService.MusicControl?=null
    private var conn:MusicPlayerServiceConn?=null

    //判断是否被解绑,默认没有
    private var isUnbind=false

    init {
        LayoutInflater.from(context).inflate(R.layout.music_player,this)
    }

    fun getSongList(songList:List<Song>,position:Int){
        list=songList
        temp=position
    }

    internal inner class MusicPlayerServiceConn:ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            musicControl=p1 as MusicPlayerService.MusicControl
        }
        override fun onServiceDisconnected(p0: ComponentName?) {
        }
    }

}
