package com.example.unlox

import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.Timer
import java.util.TimerTask

class AppMonitorService : Service() {

    private var timer: Timer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentApp = getForegroundApp()
                if (currentApp != null && currentApp != packageName) {
                    // Show a popup or launch an activity/dialog
                    Log.d("AppMonitor", "App opened: $currentApp")
                    // You can launch a popup Activity here
                }
            }
        }, 0, 2000) // check every 2 seconds

        return START_STICKY
    }

    private fun getForegroundApp(): String? {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, beginTime, endTime
        )

        if (stats != null) {
            val recentStat = stats.maxByOrNull { it.lastTimeUsed }
            return recentStat?.packageName
        }
        return null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}