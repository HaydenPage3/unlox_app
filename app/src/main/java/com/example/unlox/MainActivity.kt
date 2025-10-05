package com.example.unlox

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showPopup()
        if (!hasUsageAccessPermission()) {
            // Ask the user to grant Usage Access
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "Please grant Usage Access permission", Toast.LENGTH_LONG).show()
        } else {
            // Start monitoring service
            val serviceIntent = Intent(this, AppMonitorService::class.java)
            startService(serviceIntent)
        }
    }

    private fun showPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Welcome")
        builder.setMessage("Permissions:" + hasUsageAccessPermission())
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun hasUsageAccessPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}