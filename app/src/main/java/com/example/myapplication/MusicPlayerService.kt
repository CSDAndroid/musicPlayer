package com.example.myapplication

import Song
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import java.util.Timer
import java.util.TimerTask

class MusicPlayerService : Service() {
    private var player: MediaPlayer? =null
    private var timer: Timer?=null

    override fun onBind(intent: Intent): IBinder {
        return MusicControl()
    }

    override fun onCreate() {
        super.onCreate()
        player= MediaPlayer()
    }

    fun addTimer(){
        if(timer==null){
            timer=Timer()
            val task:TimerTask=object: TimerTask(){
                override fun run() {
                    val duration=player!!.duration
                    val currentPosition=player!!.currentPosition
                    val msg= Message()
                    val bundle= Bundle()
                    bundle.putInt("duration",duration)
                    bundle.putInt("currentPosition",currentPosition)
                    msg.data=bundle
                    MusicPlayer2.handler?.sendMessage(msg)
                }
            }
            timer!!.schedule(task,0,1000)
        }
    }

   inner class MusicControl :Binder(){
       fun play(position:Int,songList:List<Song>){
           if(position<0||position>=songList.size) return
           val uri= Uri.parse(songList[position].data)
           try {
               player?.reset()
               player?.setDataSource(applicationContext,uri)
               player?.prepare()
               player?.start()
               addTimer()
           }catch (e:Exception){
               e.printStackTrace()
           }
       }

       fun stop(){
           player?.stop()
       }

       fun pause(){
           player?.pause()
       }

       fun seekTo(progress:Int){
           player?.seekTo(progress)
       }
   }

    override fun onDestroy() {
        super.onDestroy()
        if (player!=null) {
            if (player!!.isPlaying) player!!.stop()
            player!!.release()
            player = null
        }
        timer?.cancel()
        timer = null
    }
}
