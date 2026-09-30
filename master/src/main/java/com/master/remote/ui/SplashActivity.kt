package com.master.remote.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.master.remote.R
import com.master.remote.widget.ParticleView

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_MS = 20_000L
        private const val STAGE_MS = 5_000L
    }

    private lateinit var particle: ParticleView
    private lateinit var glow: View
    private lateinit var ring: View
    private lateinit var lottie: LottieAnimationView
    private lateinit var tvBrand1: TextView
    private lateinit var tvBrand2: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tvLoading: TextView
    private lateinit var tvVersion: TextView
    private lateinit var divider: View
    private lateinit var progress: ProgressBar
    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var dot3: View

    private val handler = Handler(Looper.getMainLooper())
    private val animators = mutableListOf<ValueAnimator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) installSplashScreen()
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_splash)
        } catch (e: Exception) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        bindViews()
        runOrchestration()

        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, SPLASH_MS)
    }

    private fun bindViews() {
        particle   = findViewById(R.id.particleView)
        glow       = findViewById(R.id.glowView)
        ring       = findViewById(R.id.ringView)
        lottie     = findViewById(R.id.lottieLogo)
        tvBrand1   = findViewById(R.id.tvBrand1)
        tvBrand2   = findViewById(R.id.tvBrand2)
        tvSubtitle = findViewById(R.id.tvSubtitle)
        tvLoading  = findViewById(R.id.tvLoading)
        tvVersion  = findViewById(R.id.tvVersion)
        divider    = findViewById(R.id.dividerView)
        progress   = findViewById(R.id.progressBar)
        dot1       = findViewById(R.id.dot1)
        dot2       = findViewById(R.id.dot2)
        dot3       = findViewById(R.id.dot3)
    }

    /**
     * 20 秒动画总编排
     */
    private fun runOrchestration() {

        // ===== 0.0s 背景渐变已在 XML 里 =====

        // ===== 0.3s 粒子淡入 =====
        handler.postDelayed({
            particle.animate().alpha(1f).setDuration(1200).start()
        }, 300)

        // ===== 0.5s 光晕启动 =====
        handler.postDelayed({
            glow.animate().alpha(1f).setDuration(1000).start()
            startGlowBreathing()
        }, 500)

        // ===== 0.8s 光环旋转 + 淡入 =====
        handler.postDelayed({
            ring.alpha = 0f
            ring.animate().alpha(0.4f).setDuration(800).start()
            startRingRotation()
        }, 800)

        // ===== 1.2s Logo 弹性进入 =====
        handler.postDelayed({ startLogoSpring() }, 1200)

        // ===== 1.8s 品牌名逐字符动画 =====
        handler.postDelayed({ startBrandTypewriter() }, 1800)

        // ===== 2.8s 副标题上滑 =====
        handler.postDelayed({
            tvSubtitle.apply {
                alpha = 0f; translationY = 30f
                animate().alpha(1f).translationY(0f)
                    .setDuration(900)
                    .setInterpolator(DecelerateInterpolator()).start()
            }
        }, 2800)

        // ===== 3.2s 装饰线展开 =====
        handler.postDelayed({
            divider.apply {
                scaleX = 0f; alpha = 0f
                animate().scaleX(1f).alpha(1f)
                    .setDuration(800)
                    .setInterpolator(OvershootInterpolator(2f)).start()
            }
        }, 3200)

        // ===== 3.5s 进度条弹性展开 =====
        handler.postDelayed({
            progress.parent?.let { (it as View).alpha = 0f }
            (progress.parent as? View)?.apply {
                scaleX = 0f; scaleY = 0f; alpha = 0f
                animate().scaleX(1f).scaleY(1f).alpha(1f)
                    .setDuration(900)
                    .setInterpolator(OvershootInterpolator(1.6f)).start()
            }
        }, 3500)

        // ===== 4.0s 加载文字 + 三点 =====
        handler.postDelayed({
            tvLoading.apply {
                alpha = 0f
                animate().alpha(1f).setDuration(800).start()
            }
            dot1.alpha = 0f; dot2.alpha = 0f; dot3.alpha = 0f
            dot1.animate().alpha(1f).setDuration(400).start()
            dot2.animate().alpha(1f).setStartDelay(150).setDuration(400).start()
            dot3.animate().alpha(1f).setStartDelay(300).setDuration(400).start()
            startLoadingDots()
        }, 4000)

        // ===== 4.5s 版本号淡入 =====
        handler.postDelayed({
            tvVersion.animate().alpha(1f).setDuration(1000).start()
        }, 4500)

        // ===== 4.5s 进度条开始推进 =====
        handler.postDelayed({ startProgressAnimation() }, 4500)
    }

    /** 光晕呼吸 */
    private fun startGlowBreathing() {
        val anim = ValueAnimator.ofFloat(0.35f, 0.75f, 0.35f).apply {
            duration = 4000
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        anim.addUpdateListener {
            val f = it.animatedValue as Float
            glow.alpha = f
            glow.scaleX = 0.9f + (f - 0.35f) * 0.6f
            glow.scaleY = 0.9f + (f - 0.35f) * 0.6f
        }
        anim.start()
        animators.add(anim)
    }

    /** 外环持续旋转 */
    private fun startRingRotation() {
        ring.animate()
            .rotation(360f)
            .setDuration(15000)
            .setInterpolator(LinearInterpolator())
            .withEndAction { startRingRotation() }
            .start()
    }

    /** Logo 弹性缩放 */
    private fun startLogoSpring() {
        try {
            lottie.setAnimation(R.raw.splash_animation)
            lottie.repeatCount = LottieDrawable.INFINITE
            lottie.speed = 0.9f
            lottie.playAnimation()
        } catch (_: Exception) {}

        lottie.apply { scaleX = 0.3f; scaleY = 0.3f; alpha = 0f }
        SpringAnimation(lottie, DynamicAnimation.SCALE_X, 1f).apply {
            spring.stiffness = SpringForce.STIFFNESS_LOW
            spring.dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
            start()
        }
        SpringAnimation(lottie, DynamicAnimation.SCALE_Y, 1f).apply {
            spring.stiffness = SpringForce.STIFFNESS_LOW
            spring.dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
            start()
        }
        lottie.animate().alpha(1f).setDuration(900).start()
    }

    /** 品牌名逐字符打字机 */
    private fun startBrandTypewriter() {
        val text1 = "REMOTE "
        val text2 = "CONSOLE"
        val interval = 70L

        text1.forEachIndexed { i, _ ->
            handler.postDelayed({
                tvBrand1.text = text1.substring(0, i + 1)
                tvBrand1.alpha = 1f
                tvBrand1.translationY = -8f
                tvBrand1.animate().translationY(0f).setDuration(200).start()
            }, i * interval)
        }
        val offset1 = text1.length * interval + 100
        text2.forEachIndexed { i, _ ->
            handler.postDelayed({
                tvBrand2.text = text2.substring(0, i + 1)
                tvBrand2.alpha = 1f
                tvBrand2.translationY = -8f
                tvBrand2.animate().translationY(0f).setDuration(200).start()
            }, offset1 + i * interval)
        }
    }

    /** 三点跳动 */
    private fun startLoadingDots() {
        listOf(dot1, dot2, dot3).forEachIndexed { i, d ->
            ObjectAnimator.ofFloat(d, "translationY", 0f, -16f, 0f).apply {
                duration = 900
                startDelay = i * 150L
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }
    }

    /** 进度条推进 + 文案分段切换 */
    private fun startProgressAnimation() {
        progress.post {
            progress.max = 1000
            ObjectAnimator.ofInt(progress, "progress", 0, 1000).apply {
                duration = SPLASH_MS - 4500L
                interpolator = DecelerateInterpolator(1.2f)
                start()
            }

            val stages = listOf(
                "正在初始化核心模块..." to Color.parseColor("#4A6BFF"),
                "正在加载局域网协议栈..." to Color.parseColor("#5A6BFF"),
                "正在建立加密通道..." to Color.parseColor("#7A5BFF"),
                "即将进入控制台..." to Color.parseColor("#E94560")
            )
            stages.forEachIndexed { i, (text, color) ->
                handler.postDelayed({
                    if (isFinishing || isDestroyed) return@postDelayed
                    tvLoading.animate().alpha(0f).setDuration(200).withEndAction {
                        tvLoading.text = text
                        tvLoading.setTextColor(color)
                        tvLoading.animate().alpha(1f).setDuration(300).start()
                    }.start()
                }, i * STAGE_MS)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        animators.forEach { it.cancel() }
        try { lottie.cancelAnimation() } catch (_: Exception) {}
    }
}
