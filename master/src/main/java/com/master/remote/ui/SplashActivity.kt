package com.master.remote.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.master.remote.R

class SplashActivity : AppCompatActivity() {

    companion object { private const val SPLASH_MS = 20_000L }

    private lateinit var lottie: LottieAnimationView
    private lateinit var tvBrand: TextView
    private lateinit var tvLoading: TextView
    private lateinit var tvVersion: TextView
    private lateinit var progress: ProgressBar
    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var dot3: View

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lottie = findViewById(R.id.lottieLogo)
        tvBrand = findViewById(R.id.tvBrand)
        tvLoading = findViewById(R.id.tvLoading)
        tvVersion = findViewById(R.id.tvVersion)
        progress = findViewById(R.id.progressBar)
        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)

        entrance()
        dots()
        progressAnim()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, SPLASH_MS)
    }

    private fun entrance() {
        lottie.setAnimation(R.raw.splash_animation)
        lottie.repeatCount = LottieDrawable.INFINITE
        lottie.speed = 0.8f
        lottie.playAnimation()

        lottie.apply {
            scaleX = 0.6f; scaleY = 0.6f; alpha = 0f
            animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(1200)
                .setInterpolator(DecelerateInterpolator()).start()
        }
        tvBrand.apply {
            alpha = 0f; translationY = 60f
            animate().alpha(1f).translationY(0f).setStartDelay(600)
                .setDuration(900).setInterpolator(DecelerateInterpolator()).start()
        }
        tvLoading.apply { alpha = 0f
            animate().alpha(1f).setStartDelay(1200).setDuration(800).start() }
        tvVersion.apply { alpha = 0f
            animate().alpha(0.4f).setStartDelay(1500).setDuration(800).start() }
        progress.apply {
            scaleX = 0f
            animate().scaleX(1f).setStartDelay(1400).setDuration(800)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
        }
    }

    private fun dots() {
        listOf(dot1, dot2, dot3).forEachIndexed { i, d ->
            ObjectAnimator.ofFloat(d, "translationY", 0f, -14f, 0f).apply {
                duration = 900; startDelay = i * 150L
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator(); start()
            }
        }
    }

    private fun progressAnim() {
        progress.post {
            progress.max = 100
            ObjectAnimator.ofInt(progress, "progress", 0, 100).apply {
                duration = SPLASH_MS
                interpolator = DecelerateInterpolator(1.4f); start()
            }
            val stages = listOf(
                "正在初始化核心模块...",
                "正在加载局域网协议栈...",
                "正在建立加密通道...",
                "即将进入控制台..."
            )
            stages.forEachIndexed { i, s ->
                Handler(Looper.getMainLooper()).postDelayed({
                    tvLoading.animate().alpha(0f).setDuration(200).withEndAction {
                        tvLoading.text = s
                        tvLoading.animate().alpha(1f).setDuration(300).start()
                    }.start()
                }, i * 5000L)
            }
        }
    }

    override fun onDestroy() { super.onDestroy(); lottie.cancelAnimation() }
}
