package com.mito.kyoto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        videoView = findViewById(R.id.videoView)

        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.logo_animation}")
        videoView.setVideoURI(videoUri)

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = false
            videoView.start()
        }

        videoView.setOnCompletionListener {
            startMainActivity()
        }

        videoView.setOnErrorListener { _, _, _ ->
            startMainActivity()
            true
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                startMainActivity()
            }
        }, 5000)
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}