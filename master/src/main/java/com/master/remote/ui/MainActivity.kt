package com.master.remote.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.master.remote.R

class MainActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var tvCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_main)

        rv = findViewById(R.id.rvDevices)
        tvCount = findViewById(R.id.tvDeviceCount)
        tvCount.text = "0"
        rv.layoutManager = LinearLayoutManager(this)

        findViewById<FrameLayout>(R.id.btnScan).setOnClickListener {
            it.animate().rotationBy(360f).setDuration(800).start()
        }
        findViewById<LinearLayout>(R.id.tabCall).setOnClickListener {
            startActivity(Intent(this, CallActivity::class.java))
        }
    }
}
