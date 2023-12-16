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
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
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
        val ivMusic=findViewById<ImageView>(R.id.iv_music)
        val sharedPreferences = getSharedPreferences("isPlaying", MODE_PRIVATE)

        if(position!=null&&songList!=null){
            name!!.text= songList!![position!!].name
            play.setBackgroundResource(R.drawable.ic_play)
            val editor=sharedPreferences.edit()
            editor.putBoolean("isPlaying_${songList!![position!!].id}",false)
            editor.apply()
        }

        ivMusic.setImageResource(R.mipmap.ic_launcher_1_round)
        val rotateAnimator=RotateAnimation(0f,360f,
            Animation.RELATIVE_TO_SELF,0.5f,
            Animation.RELATIVE_TO_SELF,0.5f)
        rotateAnimator.duration=10000
        rotateAnimator.repeatCount=Animation.INFINITE

        sb?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p1==sb.max){
                    ivMusic.clearAnimation()
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val progress = sb.progress
                musicControl?.seekTo(progress)
            }
        })

        play?.setOnClickListener {
            if (position != null&&songList!=null) {
                val i=sharedPreferences.getBoolean("i_${songList!![position!!].id}",false)
                val isPlaying=sharedPreferences.getBoolean("isPlaying_${songList!![position!!].id}",false)
                if(isPlaying){
                    musicControl!!.pause()
                    play.setBackgroundResource(R.drawable.ic_play)
                    val editor=sharedPreferences.edit()
                    editor.putBoolean("isPlaying_${songList!![position!!].id}",false)
                    editor.putBoolean("i_${songList!![position!!].id}",true)
                    editor.apply()
                    ivMusic.clearAnimation()
                }else{
                    if(i){
                        musicControl?.continuePlay()
                    }else {
                        musicControl?.play(position!!, songList!!)
                    }
                    play.setBackgroundResource(R.drawable.ic_pause)
                    val editor=sharedPreferences.edit()
                    editor.putBoolean("isPlaying_${songList!![position!!].id}",true)
                    editor.putBoolean("i_${songList!![position!!].id}",false)
                    editor.apply()
                    ivMusic.startAnimation(rotateAnimator)
                }
            }
        }

        prev?.setOnClickListener {
            musicControl!!.pause()
            play.setBackgroundResource(R.drawable.ic_play)
            val editor=sharedPreferences.edit()
            editor.putBoolean("isPlaying_${songList!![position!!].id}",false)
            editor.putBoolean("i_${songList!![position!!].id}",false)
            editor.apply()
            ivMusic.clearAnimation()
            if(position!=null&&songList!=null) {
                if (position == 0) {
                        name!!.text = songList!![position!!].name
                } else {
                        position = position!! - 1
                        name!!.text = songList!![position!!].name
                }
            }
        }

        next?.setOnClickListener {
            musicControl!!.pause()
            play.setBackgroundResource(R.drawable.ic_play)
            val editor=sharedPreferences.edit()
            editor.putBoolean("isPlaying_${songList!![position!!].id}",false)
            editor.putBoolean("i_${songList!![position!!].id}",false)
            editor.apply()
            ivMusic.clearAnimation()
            if(position!=null&&songList!=null) {
                if (position == songList!!.size-1) {
                    position = 0
                    name!!.text = songList!![position!!].name
                } else {
                    position = position!! + 1
                    name!!.text = songList!![position!!].name
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