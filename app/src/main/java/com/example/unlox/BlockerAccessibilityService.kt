package com.example.unlox

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class BlockerAccessibilityService : AccessibilityService() {

    private lateinit var prefs: AppPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = AppPreferences(this)
        Log.d("BlockerService", "Accessibility connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val pkg = event.packageName?.toString() ?: return

        // If the app is NOT allowed, block it
        if (!prefs.isAllowed(pkg) && pkg != packageName) {
            showBlockScreen(pkg)
        }
    }

    private fun showBlockScreen(pkg: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this, BlockActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("blocked_pkg", pkg)
            }
            startActivity(intent)
        }, 150)
    }

    override fun onInterrupt() {}
}