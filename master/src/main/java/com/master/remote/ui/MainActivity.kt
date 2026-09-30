package com.master.remote.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.master.remote.R

class MainActivity : AppCompatActivity() {

    private lateinit var pulseRing1: View
    private lateinit var pulseRing2: View
    private lateinit var btnScan: FrameLayout

    private val pulseAnimators = mutableListOf<ValueAnimator>()
    private val activeTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        setContentView(R.layout.activity_main)

        initViews()
        startPulseAnimations()
        setupRecycler()
        setupBottomBar()
        entranceAnimations()
    }

    private fun initViews() {
        pulseRing1 = findViewById(R.id.pulseRing1)
        pulseRing2 = findViewById(R.id.pulseRing2)
        btnScan     = findViewById(R.id.btnScan)

        btnScan.setOnClickListener {
            it.animate().rotationBy(360f).setDuration(800)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
        }

        findViewById<FrameLayout>(R.id.btnNotification).setOnClickListener {
            it.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                .withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }.start()
        }
    }

    /** 扫描按钮脉冲光圈 */
    private fun startPulseAnimations() {
        animatePulse(pulseRing1, 0)
        animatePulse(pulseRing2, 1200)
    }

    private fun animatePulse(view: View, delay: Long) {
        val anim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2400
            startDelay = delay
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        anim.addUpdateListener {
            val f = it.animatedFraction
            view.scaleX = 0.9f + f * 0.5f
            view.scaleY = 0.9f + f * 0.5f
            view.alpha = (1f - f) * 0.5f
        }
        anim.start()
        pulseAnimators.add(anim)
    }

    private fun setupRecycler() {
        val rv = findViewById<RecyclerView>(R.id.rvDevices)
        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)
        rv.setItemViewCacheSize(20)

        findViewById<View>(R.id.layoutEmpty).visibility = View.VISIBLE
    }

    private fun setupBottomBar() {
        val tabConsole = findViewById<LinearLayout>(R.id.tabConsole)
        val tabCall    = findViewById<LinearLayout>(R.id.tabCall)
        val tabSettings= findViewById<LinearLayout>(R.id.tabSettings)

        tabConsole.setOnClickListener { selectTab(0) }
        tabCall.setOnClickListener {
            selectTab(1)
            startActivity(Intent(this, CallActivity::class.java))
        }
        tabSettings.setOnClickListener { selectTab(2) }
    }

    private fun selectTab(index: Int) {
        val tabs = listOf(
            Triple(findViewById<ImageView>(R.id.ivConsole), findViewById<TextView>(R.id.tvConsole), R.drawable.ic_console_active),
            Triple(findViewById<ImageView>(R.id.ivCall), findViewById<TextView>(R.id.tvCall), R.drawable.ic_call),
            Triple(findViewById<ImageView>(R.id.ivSettings), findViewById<TextView>(R.id.tvSettings), R.drawable.ic_settings)
        )

        tabs.forEachIndexed { i, (iv, tv, _) ->
            val selected = i == index
            iv.imageTintList = getColorStateList(if (selected) R.color.accent else R.color.text_tertiary)
            tv.setTextColor(getColor(if (selected) R.color.accent else R.color.text_tertiary))
            iv.animate().scaleX(if (selected) 1.1f else 1f)
                .scaleY(if (selected) 1.1f else 1f)
                .setDuration(200).start()
        }
    }

    /** 主页元素交错入场 */
    private fun entranceAnimations() {
        val statCard = findViewById<View>(R.id.statCard)

        statCard.alpha = 0f
        statCard.translationY = 40f
        statCard.animate().alpha(1f).translationY(0f)
            .setDuration(700)
            .setStartDelay(100)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        findViewById<View>(R.id.bottomBar).apply {
            alpha = 0f
            translationY = 80f
            animate().alpha(1f).translationY(0f)
                .setDuration(600)
                .setStartDelay(300)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pulseAnimators.forEach { it.cancel() }
    }
}
