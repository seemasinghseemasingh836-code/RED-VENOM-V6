package org.redvenom.overlay

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var statusView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(30, 30, 30, 30)
        layout.setBackgroundColor(Color.BLACK)

        val title = TextView(this)
        title.text = "RED VENOM V6"
        title.textSize = 30f
        title.setTextColor(Color.RED)
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "ULTIMATE GAMING HUB"
        subtitle.textSize = 16f
        subtitle.setTextColor(Color.WHITE)
        subtitle.gravity = Gravity.CENTER

        val permission = Button(this)
        permission.text = "OVERLAY PERMISSION"
        permission.setOnClickListener {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
            )
        }

        statusView = TextView(this)
        statusView.setTextColor(Color.WHITE)
        statusView.gravity = Gravity.CENTER

        val start = Button(this)
        start.text = "START OVERLAY"
        start.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, OverlayService::class.java))
                Toast.makeText(this, "Overlay started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Grant overlay permission first", Toast.LENGTH_SHORT).show()
            }
        }

        val stop = Button(this)
        stop.text = "STOP OVERLAY"
        stop.setOnClickListener {
            stopService(Intent(this, OverlayService::class.java))
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(permission)
        layout.addView(statusView)
        layout.addView(start)
        layout.addView(stop)

        updateStatus()
        setContentView(layout)
    }

    private fun updateStatus() {
        statusView.text = if (Settings.canDrawOverlays(this)) {
            "OVERLAY PERMISSION: GRANTED"
        } else {
            "OVERLAY PERMISSION: NOT GRANTED"
        }
    }

    override fun onResume() {
        super.onResume()
        if (::statusView.isInitialized) {
            updateStatus()
        }
    }
}
