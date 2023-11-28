package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
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
                }
            }
        }
    }

   inner class MusicControl():Binder(){
       fun play(i:Int){

       }

       fun pause(){
           player?.pause()
       }

       fun nextOnClick(){

       }

       fun prevOnClick(){

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