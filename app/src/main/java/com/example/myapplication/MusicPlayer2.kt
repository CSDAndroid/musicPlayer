package com.example.myapplication

import Song
import SongListAdapter
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.ComponentActivity

class MusicPlayer2 : ComponentActivity() {
    private var intent:Intent?=null
    private var songList:List<Song>?=null
    private var position:Int?=null

    private var isUnbind=false
    private var musicControl: MusicPlayerService.MusicControl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_player2)

        songList= SongListAdapter.MusicData.currentSongList
        position=SongListAdapter.MusicData.currentPosition
        handler=mHandler

        init()

        bind()
    }

    private fun bind(){
        intent=Intent(this,MusicPlayerService::class.java)
        bindService(intent,serviceConnection, BIND_AUTO_CREATE)
    }

    private fun unbind(){
       if(isUnbind){
           return
       }
        musicControl?.pause()
        unbindService(serviceConnection)
        isUnbind=true
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicControl = service as MusicPlayerService.MusicControl
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicControl=null
        }
    }

    private fun init() {
        val sb=findViewById<SeekBar>(R.id.sb)
        val play=findViewById<Button>(R.id.play)
        val prev=findViewById<Button>(R.id.prev)
        val next=findViewById<Button>(R.id.next)
        val name=findViewById<TextView>(R.id.song_name)

        if(position!=null&&songList!=null){
            name!!.text= songList!![position!!].name
        }

        sb?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val progress = sb.progress
                musicControl?.seekTo(progress)
            }
        })

        play?.setOnClickListener {
            if (position != null&&songList!=null) {
                musicControl?.play(position!!,songList!!)
            }
        }

        prev?.setOnClickListener {
            if(position!=null&&songList!=null) {
                if (position == 0) {
                    musicControl?.play(position!!,songList!!)
                    name!!.text=songList!![position!!].name
                } else {
                    position=position!!-1
                    musicControl?.play(position!!,songList!!)
                    name!!.text=songList!![position!!].name
                }
            }
        }

        next?.setOnClickListener {
            if(position!=null&&songList!=null) {
                if (position == songList?.size) {
                    position = 0
                    musicControl?.play(position!!,songList!!)
                    name!!.text=songList!![position!!].name
                } else {
                    position=position!!+1
                    musicControl?.play(position!!,songList!!)
                    name!!.text=songList!![position!!].name
                }
            }
        }
    }

    companion object{
        var handler: Handler?=null
    }
    private var mHandler=object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            val bundle=msg.data
            val currentPosition=bundle.getInt("currentPosition")
            val duration=bundle.getInt("duration")

            val sb=findViewById<SeekBar>(R.id.sb)
            sb.max=duration
            sb.progress=currentPosition

            val tvTotal=findViewById<TextView>(R.id.iv_total)
            tvTotal.text=duration.toTimeString()

            val tvProgress=findViewById<TextView>(R.id.iv_progress)
            tvProgress.text=currentPosition.toTimeString()
        }
        fun Int.toTimeString(): String {
            val minute = this / 1000 / 60
            val second = this / 1000 % 60
            return String.format("%02d:%02d", minute, second)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbind()
    }
}