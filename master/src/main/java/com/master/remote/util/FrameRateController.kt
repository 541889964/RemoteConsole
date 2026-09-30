package com.master.remote.util

/**
 * 自适应帧率控制器
 * 根据网络延迟动态调整屏幕帧传输速率
 */
class FrameRateController {

    var currentFps: Int = 15
        private set

    fun adjustByNetwork(latencyMs: Long) {
        currentFps = when {
            latencyMs < 30  -> 24
            latencyMs < 60  -> 20
            latencyMs < 100 -> 15
            latencyMs < 200 -> 10
            else            -> 5
        }
    }

    fun getFrameIntervalMs(): Long = 1000L / currentFps
}
