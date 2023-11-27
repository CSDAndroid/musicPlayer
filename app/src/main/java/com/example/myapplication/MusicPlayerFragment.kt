package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class MusicPlayerFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myFragment = MusicPlayerFragment()
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.FrameLayout_main, myFragment).commit()
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.FrameLayout_music,myFragment).commit()
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.FrameLayout_song,myFragment).commit()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_player, container, false)
    }
}