package com.example.unlox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: AppPreferences
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = AppPreferences(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24,24,24,24)
        }

        val help = TextView(this).apply {
            text = "Add the apps you want to ALLOW. All others will be blocked."
        }
        root.addView(help)


        val openAccessibilityBtn = Button(this).apply {
            text = "Open Accessibility Settings"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
        root.addView(openAccessibilityBtn)

        //Gives intent to apps
        val openOverlayBtn = Button(this).apply {
            text = "Request Display Over Other Apps"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, "Overlay allowed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        root.addView(openOverlayBtn)

        val addBtn = Button(this).apply { text = "Add allowed package" }
        root.addView(addBtn)

        listView = ListView(this)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, prefs.getAllowed().toMutableList())
        listView.adapter = adapter
        root.addView(listView)

        setContentView(root)

        addBtn.setOnClickListener {
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            AlertDialog.Builder(this)
                .setTitle("Add allowed package")
                .setMessage("Enter full package name (e.g. com.google.android.youtube)")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val pkg = input.text.toString().trim()
                    if (pkg.isNotEmpty()) {
                        prefs.addAllowed(pkg)
                        refreshList()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        listView.setOnItemLongClickListener { _, _, pos, _ ->
            val pkg = adapter.getItem(pos) ?: ""
            AlertDialog.Builder(this)
                .setTitle("Remove blocked package?")
                .setMessage(pkg)
                .setPositiveButton("Remove") { _, _ ->
                    prefs.removeAllowed(pkg)
                    refreshList()
                }
                .setNegativeButton("Cancel", null)
                .show()
            true
        }
    }

    private fun refreshList() {
        adapter.clear()
        adapter.addAll(prefs.getAllowed().toList())
        adapter.notifyDataSetChanged()
    }
}