package com.example.unlox

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BlockActivity : AppCompatActivity() {
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = AppPreferences(this)

        // make full screen and keep above lock/other apps
        window.addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val root = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 150, 50, 50)
        }

        val pkg = intent.getStringExtra("blocked_pkg") ?: "Blocked App"
        val title = TextView(this).apply {
            textSize = 22f
            text = "This app is blocked:\n$pkg"
        }
        root.addView(title)

        val info = TextView(this).apply {
            text = "You chose to block this app. If this is an emergency, press 'Exit'."
            setPadding(0,30,0,30)
        }
        root.addView(info)

        val exitBtn = Button(this).apply {
            text = "Exit"
            setOnClickListener {
                // Option: Allow temporary exit by user confirmation
                AlertDialog.Builder(this@BlockActivity)
                    .setTitle("Exit Block")
                    .setMessage("Are you sure you want to exit the blocked app? This will open the app.")
                    .setPositiveButton("Yes") { _, _ -> finish() }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
        root.addView(exitBtn)

        val removeBlockBtn = Button(this).apply {
            text = "Remove Block (one-time)"
            setOnClickListener {
                prefs.removeBlocked(pkg)
                finish()
            }
        }
        root.addView(removeBlockBtn)

        setContentView(root)
    }

    override fun onBackPressed() {
        // prevent back button from dismissing the block (optional)
    }
}