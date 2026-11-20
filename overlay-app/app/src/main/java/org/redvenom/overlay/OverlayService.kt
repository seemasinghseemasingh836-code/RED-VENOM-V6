package org.redvenom.overlay

import android.app.Service
import android.net.ConnectivityManager
import android.os.SystemClock
import android.app.ActivityManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class OverlayService : Service() {

    private var wm: WindowManager? = null
    private var root: LinearLayout? = null
    private var sessionStartedAt = 0L

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (root != null) return START_NOT_STICKY

        sessionStartedAt = SystemClock.elapsedRealtime()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(dp(8), dp(8), dp(8), dp(8))
        layout.setBackgroundColor(Color.BLACK)

        val toggle = TextView(this)
        toggle.text = "🔴 RED VENOM"
        toggle.textSize = 16f
        toggle.setTextColor(Color.RED)
        toggle.setPadding(dp(14), dp(12), dp(14), dp(12))

        val menu = LinearLayout(this)
        menu.orientation = LinearLayout.VERTICAL
        menu.visibility = android.view.View.GONE

        val features = listOf(
            "CPU Information",
            "RAM Monitor",
            "Storage Monitor",
            "Aim Accuracy Tracker",
            "Gaming Session Timer",
            "Device Information",
            "Network Status"
        )

        features.forEach { feature ->
            val item = TextView(this)
            item.text = "• $feature"
            item.textSize = 14f
            item.setTextColor(Color.WHITE)
            item.setPadding(dp(12), dp(10), dp(12), dp(10))
            item.setOnClickListener {
                val message = when (feature) {
                    "CPU Information" -> "CPU Cores: ${Runtime.getRuntime().availableProcessors()}\nArchitecture: ${System.getProperty("os.arch")}"
                    "RAM Monitor" -> {
                        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
                        val mi = ActivityManager.MemoryInfo()
                        am.getMemoryInfo(mi)
                        "Total RAM: ${mi.totalMem / 1048576} MB\nFree RAM: ${mi.availMem / 1048576} MB"
                    }
                    "Storage Monitor" -> {
                        val st = StatFs(Environment.getDataDirectory().path)
                        "Total: ${st.totalBytes / 1073741824} GB\nFree: ${st.availableBytes / 1073741824} GB"
                    }
                    "Device Information" -> "Device: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid: ${Build.VERSION.RELEASE}"
                    "Gaming Session Timer" -> {
                        val seconds = (SystemClock.elapsedRealtime() - sessionStartedAt) / 1000
                        "Gaming Session: ${seconds / 3600}h ${(seconds % 3600) / 60}m ${seconds % 60}s"
                    }
                    "Network Status" -> {
                        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                        val network = cm.activeNetworkInfo
                        if (network != null && network.isConnected) "Network: Connected (${network.typeName})"
                        else "Network: Disconnected"
                    }
                    "Aim Accuracy Tracker" -> "Automatic aim detection needs game-specific integration."
                    else -> "$feature: Next development step"
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
            menu.addView(item)
        }

        toggle.setOnClickListener {
            menu.visibility =
                if (menu.visibility == android.view.View.VISIBLE)
                    android.view.View.GONE
                else android.view.View.VISIBLE
        }

        layout.addView(toggle)
        layout.addView(menu)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = dp(20)
        params.y = dp(150)

        root = layout
        wm?.addView(layout, params)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        root?.let { wm?.removeView(it) }
        root = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
