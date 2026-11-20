package org.redvenom.overlay

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var statusView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(30, 40, 30, 40)
        layout.setBackgroundColor(Color.BLACK)

        val title = TextView(this)
        title.text = "RED VENOM V6"
        title.textSize = 30f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.RED)

        val subtitle = TextView(this)
        subtitle.text = "ULTIMATE GAMING HUB"
        subtitle.textSize = 14f
        subtitle.gravity = Gravity.CENTER
        subtitle.setTextColor(Color.WHITE)

        statusView = TextView(this)
        statusView.textSize = 16f
        statusView.gravity = Gravity.CENTER
        statusView.setTextColor(Color.GREEN)

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

        val start = Button(this)
        start.text = "START GAMING MODE"
        start.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, OverlayService::class.java))
                Toast.makeText(this, "Gaming Mode Started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Grant Overlay Permission First", Toast.LENGTH_SHORT).show()
            }
        }

        val stop = Button(this)
        stop.text = "STOP GAMING MODE"
        stop.setOnClickListener {
            stopService(Intent(this, OverlayService::class.java))
            Toast.makeText(this, "Gaming Mode Stopped", Toast.LENGTH_SHORT).show()
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(statusView)
        layout.addView(permission)
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)
        updateStatus()
    }

    private fun updateStatus() {
        statusView.text = if (Settings.canDrawOverlays(this)) {
            "OVERLAY: GRANTED"
        } else {
            "OVERLAY: NOT GRANTED"
        }
    }

    override fun onResume() {
        super.onResume()
        if (::statusView.isInitialized) updateStatus()
    }
}
