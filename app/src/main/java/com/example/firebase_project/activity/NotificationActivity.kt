package com.example.firebase_project.activity

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_project.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_notification.*


class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("c", token)
            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })

    }

    override fun onResume() {
        super.onResume()
        init()
        setSwitchChangeListener()
    }

    private fun init() {
        val sharedPreferences = getSharedPreferences("SharedPref", MODE_PRIVATE)
        val notificationStatus = sharedPreferences.getBoolean("NotificationSwitchStatus", false)
        switchNotification.isChecked = notificationStatus
    }

    private fun setSwitchChangeListener() {
        val sharedPreferences = getSharedPreferences("SharedPref", MODE_PRIVATE)
        val switchEditor = sharedPreferences.edit()
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchEditor.putBoolean("NotificationSwitchStatus", switchNotification.isChecked)
                switchEditor.apply()
                FirebaseMessaging.getInstance().subscribeToTopic("main_notification")
                Toast.makeText(applicationContext, "enabled notification", Toast.LENGTH_LONG).show()
            } else {
                switchEditor.putBoolean("NotificationSwitchStatus", switchNotification.isChecked)
                switchEditor.apply()
                FirebaseMessaging.getInstance().unsubscribeFromTopic("main_notification")
                Toast.makeText(applicationContext, "disabled notification", Toast.LENGTH_LONG).show()
            }
        }
    }
}