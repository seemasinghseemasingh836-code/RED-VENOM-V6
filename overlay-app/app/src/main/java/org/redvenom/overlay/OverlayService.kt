package org.redvenom.overlay

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: TextView? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (overlayView == null) {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            val view = TextView(this)
            view.text = "🔴 RED VENOM V6"
            view.textSize = 16f
            view.setTextColor(Color.RED)
            view.setBackgroundColor(Color.BLACK)
            view.setPadding(24, 16, 24, 16)

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

            params.gravity = Gravity.TOP or Gravity.START
            params.x = 30
            params.y = 150

            overlayView = view
            windowManager?.addView(view, params)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        overlayView?.let {
            windowManager?.removeView(it)
        }
        overlayView = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
