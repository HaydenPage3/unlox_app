package com.example.unlox

import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.accessibilityservice.AccessibilityService
import android.util.Log

class BlockerAccessibilityService : AccessibilityService() {

    private lateinit var prefs: AppPreferences
    private var lastShownPackage: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = AppPreferences(this)
        // adds unlox to the whitelist
        prefs.addAllowed("com.unloxapp.android")
        prefs.addAllowed("com.example.unlox")
        prefs.addAllowed("com.google.android.inputmethod.latin")
        prefs.addAllowed("com.google.android.apps.nexuslauncher")
        getLauncherPackage()?.let { prefs.addAllowed(it) }
        Log.d("BlockerService", "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            val pkg = event.packageName?.toString() ?: return

            // Block if NOT in whitelist
            if (!prefs.isAllowed(pkg)) {
                showBlockScreen(pkg)
            }
        }
    }

    private fun showBlockScreen(pkg: String) {
        val intent = Intent(this, BlockActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("blocked_pkg", pkg)
        }
        startActivity(intent)
    }

    private fun getLauncherPackage(): String? {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.resolveActivity(intent, 0)
        return resolveInfo?.activityInfo?.packageName
    }

    override fun onInterrupt() {}
}