package com.example.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView

class MusicPlayer(context: Context, attrs: AttributeSet?):RelativeLayout(context,attrs) {
    private var sb: SeekBar? = null
    private var progress: TextView? = null
    private var total: TextView? = null
    private var artist: TextView? = null
    private var name: TextView? = null
    private var prev: Button? = null
    private var play: Button? = null
    private var next: Button? = null

    private var musicControl:MusicPlayerService.MusicControl?=null
    private var isUnbind=false

    private val serviceConnection=object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicControl=service as MusicPlayerService.MusicControl
            isUnbind=true
        }
        override fun onServiceDisconnected(p0: ComponentName?) {
            isUnbind=false
        }
    }

    private fun init() {
        sb!!.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val progress = sb!!.progress
                musicControl?.seekTo(progress)
            }
        })
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.music_player, this)
        sb=findViewById(R.id.SeekBar)
        progress=findViewById(R.id.tv_progress)
        total=findViewById(R.id.tv_total)
        artist=findViewById(R.id.artist)
        name=findViewById(R.id.Name)
        play=findViewById(R.id.onClickBroadcast)
        prev=findViewById(R.id.onClickLastSong)
        next=findViewById(R.id.onClickNextSong)

        val intent=Intent(context,MusicPlayerService::class.java)
        context.bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE)

        init()
    }

//    fun getSongList(songList: List<Song>, position: Int) {
//        val songList=songList
//        val position=position
//    }

    private fun unbind(isUnbind: Boolean) {
        if (!isUnbind) {
            musicControl?.pause()
            context.unbindService(serviceConnection!!)
        }
    }

    companion object {
        private var sb: SeekBar? = null
        private var tv_progress: TextView? = null
        private var tv_total: TextView? = null

        var handler: Handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                val duration = bundle.getInt("duration")
                val currentPosition = bundle.getInt("currentPosition")

                sb!!.max = duration
                sb!!.progress = currentPosition

                var minute = duration / 1000 / 60
                var second = duration / 1000 % 60
                var strMinute: String? = null
                var strSecond: String? = null
                strMinute = if (minute < 10) {
                    "0$minute"
                } else {
                    minute.toString() + ""
                }
                strSecond = if (second < 10) {
                    "0$second"
                } else {
                    second.toString() + ""
                }
                tv_total!!.text = "$strMinute:$strSecond"

                var minute1 = currentPosition / 1000 / 60
                var second1 = currentPosition / 1000 % 60
                var strMinute1: String? = null
                var strSecond1: String? = null
                strMinute1 = if (minute1 < 10) {
                    "0$minute1"
                } else {
                    minute1.toString() + ""
                }
                strSecond1 = if (second1 < 10) {
                    "0$second1"
                } else {
                    second1.toString() + ""
                }
                tv_progress!!.text = "$strMinute1:$strSecond1"
            }
        }
    }
}