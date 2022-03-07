package com.example.firebase_project.background

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import com.example.firebase_project.R
import com.example.firebase_project.activity.NotificationActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageService : FirebaseMessagingService(){
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private lateinit var pendingIntent: PendingIntent

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token) }
    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }
    override fun onMessageReceived(p0: RemoteMessage) {
        val myProcess = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(myProcess)
        val title= p0.notification?.title
        val message= p0.notification?.body
        notificationCreate(title.toString(), message.toString())
        val isInForeground = (myProcess.importance ==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
        if(isInForeground) {
            customNotificationCreate(title.toString(), message.toString())
        } else{
            notificationCreate(title.toString(), message.toString())
        }
        super.onMessageReceived(p0)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun notificationCreate(title: String, shortDescription: String) {
        val notificationIntent = Intent(this, NotificationActivity::class.java)
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(
                    ConstantString.channelId,
                    ConstantString.description  ,
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(applicationContext, ConstantString.channelId)
                .setSmallIcon(R.drawable.ic_baseline_notifications)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(shortDescription)
        } else {
            builder = Notification.Builder(applicationContext)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_baseline_notifications)
                .setContentText(shortDescription)
        }
        notificationManager.notify(1234, builder.build())
    }
    private fun customNotificationCreate(title: String, shortDescription: String) {
        val notificationLayout = RemoteViews(packageName,R.layout.custom_notification)
        notificationLayout.setTextViewText(R.id.tvTitle, title)
        notificationLayout.setTextViewText(R.id.tvMessage, shortDescription)


        val notificationIntent = Intent(this, NotificationActivity::class.java)
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(
                    ConstantString.channelId,
                    ConstantString.description  ,
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(applicationContext, ConstantString.channelId)
                .setSmallIcon(R.drawable.ic_baseline_notifications)
                .setContentIntent(pendingIntent)
                .setCustomContentView(notificationLayout)
        } else {
            builder = Notification.Builder(applicationContext)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_baseline_notifications)
                .setContentText(shortDescription)
        }
        notificationManager.notify(1234, builder.build())
    }


}