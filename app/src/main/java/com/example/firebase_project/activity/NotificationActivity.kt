package com.example.firebase_project.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_project.R
import com.example.firebase_project.background.BatteryLowService
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {
    private lateinit var  intentService:Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        setSwitchChangeListener()
    }

    private fun setSwitchChangeListener() {
        switchNotification.setOnCheckedChangeListener {_, _ ->
            if(switchNotification.isChecked){
               intentService = Intent(this, BatteryLowService::class.java)
                startService(intentService)
            }
            if(!switchNotification.isChecked){
                stopService(intentService)
            }
        }
    }
}