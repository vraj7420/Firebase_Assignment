package com.example.firebase_project.background

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

class BatteryLowService : Service() {
    private lateinit var batteryLowReceiver: BatteryLowReceiver


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        batteryLowReceiver = BatteryLowReceiver()
        registerReceiver(batteryLowReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        return super.onStartCommand(intent, flags, startId)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        unregisterReceiver(batteryLowReceiver)

    }

}