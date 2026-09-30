package com.master.remote.util

import android.os.SystemClock

/**
 * 触摸事件节流器
 * 用于远程操作时限制发送频率，避免网络拥塞
 */
class TouchThrottle(private val minIntervalMs: Long = 16L) {
    private var lastSentAt = 0L

    fun allow(): Boolean {
        val now = SystemClock.uptimeMillis()
        if (now - lastSentAt < minIntervalMs) return false
        lastSentAt = now
        return true
    }

    fun reset() { lastSentAt = 0L }
}
