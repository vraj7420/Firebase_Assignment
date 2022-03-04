package com.example.firebase_project.background

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import com.example.firebase_project.R
import com.example.firebase_project.activity.NotificationActivity


class BatteryLowReceiver : BroadcastReceiver() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private lateinit var pendingIntent: PendingIntent


    @SuppressLint("UnspecifiedImmutableFlag", "UnsafeProtectedBroadcastReceiver")
    override fun onReceive(ctx: Context?, intent: Intent?) {

        val batteryStatus = intent?.getIntExtra("level", 0)
        val notificationIntent = Intent(ctx, NotificationActivity::class.java)
        val myProcess = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(myProcess)
        val isForGround = (myProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
        notificationManager = ctx?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        pendingIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (batteryStatus != null) {
            if (batteryStatus < 15) {
                if (isForGround) {
                    customNotificationCreate(
                        ctx, R.drawable.ic_battery_low,
                        ctx.getString(R.string.battery_low),
                        ctx.getString(R.string.battery_low_short_description)
                    )
                }else{
                    notificationCreate(
                        ctx, R.drawable.ic_battery_low,
                        ctx.getString(R.string.battery_low),
                        ctx.getString(R.string.battery_low_short_description)
                    )
                }
            }

        }
    }

    private fun notificationCreate(
        ctx: Context?,
        smallIcon: Int,
        title: String,
        shortDescription: String
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(
                    "Notification",
                    "set Notification",
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(ctx, "Notification")
                .setSmallIcon(smallIcon)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(shortDescription)


        } else {

            builder = Notification.Builder(ctx)
                .setSmallIcon(smallIcon)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(shortDescription)

        }
        notificationManager.notify(1234, builder.build())
    }
    @SuppressLint("RemoteViewLayout")
    private fun customNotificationCreate(
        ctx: Context?,
        smallIcon: Int,
        title: String,
        shortDescription: String
    ) {
        val contentView = RemoteViews(ctx?.packageName, R.layout.custom_notification)
        contentView.setTextViewText(R.id.tvNotificationTitle,title)
        contentView.setTextViewText(R.id.tvNotificationDescription,shortDescription)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel("Notification", "set Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(ctx, "Notification")
                .setCustomContentView(contentView)
                .setContentIntent(pendingIntent)

        } else {
                builder = Notification.Builder(ctx)
                    .setSmallIcon(smallIcon)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)


            }
        notificationManager.notify(1234, builder.build())
    }

}