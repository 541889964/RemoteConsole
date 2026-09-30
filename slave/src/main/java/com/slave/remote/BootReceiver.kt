package com.slave.remote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val i = Intent(context, RemoteControlService::class.java)
            try { context.startService(i) } catch (_: Exception) {}
        }
    }
}
