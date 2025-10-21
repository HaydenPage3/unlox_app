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
        Log.d("BlockerService", "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // Focus on window state change to detect new foreground activities
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            val pkg = event.packageName?.toString() ?: return

            // If package is blocked and not already showing blocker
            if (prefs.isBlocked(pkg)) {
                // avoid repeatedly launching BlockActivity for same package
                if (lastShownPackage != pkg) {
                    lastShownPackage = pkg
                    showBlockScreen(pkg)
                }
            } else {
                // if user leaves blocked app, dismiss block
                if (lastShownPackage == pkg) {
                    lastShownPackage = null
                }
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

    override fun onInterrupt() {}
}