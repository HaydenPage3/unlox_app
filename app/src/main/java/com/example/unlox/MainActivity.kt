package com.example.unlox

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: AppPreferences
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = AppPreferences(this)

        val scroll = ScrollView(this)
        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val info = TextView(this).apply {
            text = "All apps are blocked unless allowed below. Toggle apps to whitelist them."
        }
        container.addView(info)

        val openAccessibilityBtn = Button(this).apply {
            text = "Open Accessibility Settings"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
        container.addView(openAccessibilityBtn)

        val openOverlayBtn = Button(this).apply {
            text = "Allow Overlay Permission"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, "Overlay already granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
        container.addView(openOverlayBtn)

        scroll.addView(container)
        setContentView(scroll)

        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        container.removeAllViewsInLayout()
        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null } // only launchable apps
            .sortedBy { it.loadLabel(pm).toString().lowercase() }

        for (app in apps) {
            addAppToggle(app)
        }
    }

    private fun addAppToggle(app: ApplicationInfo) {
        val pm = packageManager
        val appName = app.loadLabel(pm).toString()
        val appIcon = app.loadIcon(pm)
        val pkg = app.packageName
        val isAllowed = prefs.isAllowed(pkg)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 12, 0, 12)
        }

        val iconView = ImageView(this).apply {
            setImageDrawable(appIcon)
            layoutParams = LinearLayout.LayoutParams(100, 100)
        }

        val label = TextView(this).apply {
            text = appName
            textSize = 16f
            setPadding(16, 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val toggle = Switch(this).apply {
            isChecked = isAllowed
            setOnCheckedChangeListener { _, checked ->
                if (checked) prefs.addAllowed(pkg) else prefs.removeAllowed(pkg)
            }
        }

        layout.addView(iconView)
        layout.addView(label)
        layout.addView(toggle)
        container.addView(layout)
    }
}