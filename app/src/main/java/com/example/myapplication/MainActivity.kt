package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    private val REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        //检查权限是否被处理
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
            //请求权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        } else {
            // 已获得权限，执行其他初始化代码
            initialize()
        }
    }

    //处理权限请求结果
    @Deprecated("This method is deprecated",ReplaceWith("newMethod"))
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED&&grantResults[1]
                    == PackageManager.PERMISSION_GRANTED) {
                    initialize()
                } else {
                    finish()
                }
            }
        }
    }

    private fun initialize() {
        val like=findViewById<Button>(R.id.Like)
        val local=findViewById<Button>(R.id.local)
        val all=findViewById<Button>(R.id.all)

        like.setOnClickListener{
            val intent1= Intent(this,MusicListActivity::class.java)
            startActivity(intent1)
        }

        local.setOnClickListener{
            val intent=Intent(this,SongListActivity::class.java)
            startActivity(intent)
        }

        all.setOnClickListener {
            val intent2=Intent(this,HttpsMusic::class.java)
            startActivity(intent2)
        }
    }
}