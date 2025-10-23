package com.example.unlox

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class BlockActivity : AppCompatActivity() {
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = AppPreferences(this)

        // Make it full screen
        window.addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        // Background container
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF4A86E8.toInt()) // dark gray background
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }

        // CardView for content
        val card = CardView(this).apply {
            radius = 30f
            cardElevation = 0f
            setCardBackgroundColor(0xFF4A86E8.toInt())
            useCompatPadding = true
            setContentPadding(60, 80, 60, 80)
        }

        val pkg = intent.getStringExtra("blocked_pkg") ?: "Blocked App"

        val cardLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = "Unlox"
            textSize = 48f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
        }

        val unloxImage = ImageView(this).apply {
            setImageResource(R.drawable.unlox_logo)   // your image file
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            layoutParams = LinearLayout.LayoutParams(600, 600).apply {
                topMargin = 20
                bottomMargin = 20
                gravity = Gravity.CENTER
            }
        }

        val subtitle = TextView(this).apply {
            text = "\n$pkg is blocked."
            textSize = 18f
            setTextColor(0xFF000000.toInt())
            gravity = Gravity.CENTER
        }

        val message = TextView(this).apply {
            text = ""//no text here on purpose -- add a message
            textSize = 15f
            setTextColor(0xFFAAAAAA.toInt())
            gravity = Gravity.CENTER
        }

        val btnBack = Button(this).apply {
            text = "Go Back"
            textSize = 16f
            setOnClickListener {
                // Simply close this block screen
                finish()
            }
        }

        val btnUnblock = Button(this).apply {
            text = "Unlox App"
            textSize = 16f
            setOnClickListener {
                prefs.removeAllowed(pkg)
                finish()
            }
        }

        val btnLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 0)
            addView(btnBack)
            addView(btnUnblock)
            btnBack.setPadding(40, 20, 40, 20)
            btnUnblock.setPadding(40, 20, 40, 20)
        }

        cardLayout.addView(title)
        cardLayout.addView(subtitle)
        cardLayout.addView(unloxImage)
        cardLayout.addView(message)
        cardLayout.addView(btnLayout)
        card.addView(cardLayout)

        root.addView(card)
        setContentView(root)
    }

    override fun onBackPressed() {
        // Prevent back press from bypassing the block
    }
}