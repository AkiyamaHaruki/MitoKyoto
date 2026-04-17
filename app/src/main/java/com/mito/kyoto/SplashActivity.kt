package com.mito.kyoto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.VideoView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置全局异常捕获，将崩溃日志写入文件
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            saveCrashLog(throwable)
            // 仍然显示系统崩溃对话框或退出
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        
        setContentView(R.layout.activity_splash)

        videoView = findViewById(R.id.videoView)

        try {
            // 加载本地视频文件（目前是空文件占位）
            val videoUri = Uri.parse("android.resource://$packageName/${R.raw.logo_animation}")
            videoView.setVideoURI(videoUri)

            videoView.setOnPreparedListener { mp ->
                mp.isLooping = false
                videoView.start()
            }

            videoView.setOnCompletionListener {
                startMainActivity()
            }

            videoView.setOnErrorListener { _, what, extra ->
                // 视频播放出错（因为是空文件），直接跳转主界面，不崩溃
                startMainActivity()
                true
            }
        } catch (e: Exception) {
            // 任何意外异常也直接跳转
            e.printStackTrace()
            startMainActivity()
        }

        // 超时保护：最多等待5秒后强制跳转
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                startMainActivity()
            }
        }, 5000)
    }

    private fun saveCrashLog(throwable: Throwable) {
        try {
            val logFile = File(getExternalFilesDir(null), "crash_log.txt")
            val fos = FileOutputStream(logFile, true)
            val pw = PrintWriter(fos)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            pw.println("========== ${sdf.format(Date())} ==========")
            throwable.printStackTrace(pw)
            pw.close()
        } catch (_: Exception) {
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}