package com.example.systemtoolkit

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat

/** Foreground service that draws a floating overlay above other apps. */
class OverlayService : Service() {
    private var view: TextView? = null
    private var manager: WindowManager? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, notification())
        if (view == null) showOverlay()
        return START_STICKY
    }

    private fun notification(): Notification {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "System overlay", NotificationManager.IMPORTANCE_LOW)
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AppForge system app")
            .setContentText("Overlay service is running")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }

    private fun showOverlay() {
        if (!Settings.canDrawOverlays(this)) return
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        manager = wm
        val label = TextView(this)
        label.text = "AppForge overlay"
        label.textSize = 14f
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 24
        params.y = 120
        wm.addView(label, params)
        view = label
    }

    override fun onDestroy() {
        view?.let { manager?.removeView(it) }
        view = null
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "system_overlay"
        const val NOTIFICATION_ID = 42
    }
}
