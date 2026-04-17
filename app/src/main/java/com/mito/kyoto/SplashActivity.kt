package com.mito.kyoto

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private var hasNavigated = false
    private val handler = Handler(Looper.getMainLooper())

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // 直接跳转主界面，不加载视频
    startActivity(Intent(this, MainActivity::class.java))
    finish()
}

/*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        videoView = findViewById(R.id.videoView)

        // 设置视频源
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.logo_animation}")
        videoView.setVideoURI(videoUri)

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = false
            videoView.start()
        }

        videoView.setOnCompletionListener {
            navigateToMain()
        }

        videoView.setOnErrorListener { _, what, extra ->
            // 视频播放出错时直接跳转
            navigateToMain()
            true
        }

        // 超时保护：无论视频是否准备好，5秒后强制跳转
        handler.postDelayed({
            if (!hasNavigated) {
                navigateToMain()
            }
        }, 5000)
    }
*/

    private fun navigateToMain() {
        if (hasNavigated) return
        hasNavigated = true
        handler.removeCallbacksAndMessages(null)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}