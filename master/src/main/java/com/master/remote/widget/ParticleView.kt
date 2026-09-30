package com.master.remote.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

/**
 * 漂浮粒子背景
 * 30 个粒子，各自有不同速度、大小、透明度，向上升起并循环
 */
class ParticleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private data class Particle(
        var x: Float,
        var y: Float,
        val radius: Float,
        val speed: Float,
        val alpha: Int,
        val color: Int
    )

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var initialized = false

    private val colors = intArrayOf(
        Color.parseColor("#4A6BFF"),
        Color.parseColor("#7A5BFF"),
        Color.parseColor("#5A6BFF"),
        Color.parseColor("#E94560")
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!initialized) {
            initParticles(w, h)
            initialized = true
        }
    }

    private fun initParticles(w: Int, h: Int) {
        particles.clear()
        repeat(30) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * w,
                    y = Random.nextFloat() * h,
                    radius = Random.nextFloat() * 3f + 1f,
                    speed = Random.nextFloat() * 0.8f + 0.3f,
                    alpha = (Random.nextInt(80, 200)),
                    color = colors.random()
                )
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (p in particles) {
            paint.color = p.color
            paint.alpha = p.alpha
            canvas.drawCircle(p.x, p.y, p.radius, paint)

            // 向上漂浮
            p.y -= p.speed
            if (p.y < -10f) {
                p.y = height + 10f
                p.x = Random.nextFloat() * width
            }
        }
        postInvalidateOnAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        particles.clear()
    }
}
