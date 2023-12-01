package com.example.myapplication

import Song
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
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
            val task:TimerTask=object:TimerTask(){
                override fun run() {
                    if(player==null)return
                    val duration=player!!.duration
                    val currentPosition=player!!.currentPosition
                    val msg=MusicPlayer.handler.obtainMessage()
                    val bundle=Bundle()
                    bundle.putInt("duration",duration)
                    bundle.putInt("currentPosition",currentPosition)
                    msg.data=bundle
                    MusicPlayer.handler.handleMessage(msg)
                }
            }
            timer!!.schedule(task,5,500)
        }
    }

   inner class MusicControl():Binder(){
       fun play(position:Int,songList:List<Song>){
           val uri= Uri.parse(songList[position].data)
           try {
               player?.reset()
               MediaPlayer().setDataSource(applicationContext,uri)
               player?.start()
               addTimer()
           }catch (e:Exception){
               e.printStackTrace()
           }
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
        if(player==null)return
        if(player!!.isPlaying) player!!.stop()
        player!!.release()
        player=null
    }
}
