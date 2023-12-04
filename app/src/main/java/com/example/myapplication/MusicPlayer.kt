package com.example.myapplication

import MusicData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import java.lang.ref.WeakReference

class MusicPlayer(context: Context, attrs: AttributeSet?):RelativeLayout(context,attrs) {
    private var sb: SeekBar? = null
    private var progress: TextView? = null
    private var total: TextView? = null
    private var artist: TextView? = null
    private var name: TextView? = null
    private var play: Button? = null
    private var prev: Button? = null
    private var next: Button? = null

    private var musicControl: MusicPlayerService.MusicControl? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("o", "Service connected")
            musicControl = service as MusicPlayerService.MusicControl
        }
        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    private var songList=MusicData.currentSongList
    private var position=MusicData.currentPosition

    init {
        LayoutInflater.from(context).inflate(R.layout.music_player, this)
        sb = findViewById(R.id.SeekBar)
        progress = findViewById(R.id.tv_progress)
        total = findViewById(R.id.tv_total)
        artist = findViewById(R.id.artist)
        name = findViewById(R.id.Name)
        play = findViewById(R.id.onClickBroadcast)
        prev = findViewById(R.id.onClickLastSong)
        next = findViewById(R.id.onClickNextSong)

        val intent = Intent(context, MusicPlayerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        init()
    }

    private fun init() {
        play!!.setBackgroundResource(R.drawable.ic_pause)
        name!!.text= position.let { songList[it].name }
        artist!!.text= position.let { songList[it].artist }
        position.let { it1 -> songList.let { it2 -> musicControl?.play(it1, it2) } }

        sb!!.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                val progress = sb!!.progress
                musicControl?.seekTo(progress)
            }
        })

        val sharedPreference = context.getSharedPreferences("isPlaying", Context.MODE_PRIVATE)

        play!!.setOnClickListener {
            val song = position.let { it1 -> songList[it1] }
            val isPlaying = sharedPreference.getBoolean("isPlaying_${song.id}", false)
            if (isPlaying) {
                play!!.setBackgroundResource(R.drawable.ic_play)
                musicControl?.pause()
            } else {
                play!!.setBackgroundResource(R.drawable.ic_pause)
                position.let { it1 -> songList.let { it2 -> musicControl?.play(it1, it2) } }
            }
        }

        prev!!.setOnClickListener {
            if (position == 0) {
                songList.let { it1 -> musicControl?.play(position, it1) }
            }else {
                position -= 1
                name!!.text = position.let { songList[it].name }
                artist!!.text = position.let { songList[it].artist }
                songList.let { it1 -> musicControl?.play(position, it1) }
            }
        }

        next!!.setOnClickListener {
            if(position>songList.size-1){
                position=0
                songList.let { it1 -> musicControl?.play(position, it1) }
            }else {
                position += 1
                name!!.text = position.let { songList[it].name }
                artist!!.text = position.let { songList[it].artist }
                songList.let { it1 -> musicControl?.play(position, it1) }
            }
        }
    }

//        companion object {
//        private var sb: SeekBar? = null
//        private var tv_progress: TextView? = null
//        private var tv_total: TextView? = null
//
//        var handler: Handler = object : Handler() {
//            override fun handleMessage(msg: Message) {
//                val bundle = msg.data
//                val duration = bundle.getInt("duration")
//                val currentPosition = bundle.getInt("currentPosition")
//
//                sb!!.max = duration
//                sb!!.progress = currentPosition
//
//                val minute = duration / 1000 / 60
//                val second = duration / 1000 % 60
//                val strMinute=if(minute<10){
//                    "0$minute"
//                }else{
//                    minute.toString()+""
//                }
//                val strSecond=if(second<10){
//                    "0$second"
//                }else{
//                    second.toString()+""
//                }
//                tv_total!!.text="$strMinute:$strSecond"
//
//                val minute1 = currentPosition / 1000 / 60
//                val second1 = currentPosition / 1000 % 60
//                val strMinute1=if(minute1<10){
//                    "0$minute1"
//                }else{
//                    minute1.toString()+""
//                }
//                val strSecond1=if(second1<10){
//                    "0$second1"
//                }else{
//                    second1.toString()+""
//                }
//                tv_progress!!.text="$strMinute1:$strSecond1"
//            }
//        }
//    }


    companion object {
        private lateinit var sbRef: WeakReference<SeekBar>
        private lateinit var tvProgressRef: WeakReference<TextView>
        private lateinit var tvTotalRef: WeakReference<TextView>

        val handler: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val bundle = msg.data
                val duration = bundle.getInt("duration")
                val currentPosition = bundle.getInt("currentPosition")

                val sb = sbRef.get()
                sb?.max = duration
                sb?.progress = currentPosition

                val tvTotal = tvTotalRef.get()
                tvTotal?.text = tvTotal?.resources?.getString(R.string.total_time, duration.toTimeString())

                val tvProgress = tvProgressRef.get()
                tvProgress?.text = tvProgress?.resources?.getString(R.string.current_time, currentPosition.toTimeString())
            }
        }

        fun setViews(sb: SeekBar, tvProgress: TextView, tvTotal: TextView) {
            sbRef = WeakReference(sb)
            tvProgressRef = WeakReference(tvProgress)
            tvTotalRef = WeakReference(tvTotal)
        }

        fun Int.toTimeString(): String {
            val minute = this / 1000 / 60
            val second = this / 1000 % 60
            return String.format("%02d:%02d", minute, second)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        context.unbindService(serviceConnection)
    }
}

