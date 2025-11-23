package com.owesome.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.owesome.R
import com.owesome.data.repository.NotificationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay
import kotlin.time.Duration.Companion.seconds

const val CHANNEL_ID = "owesome_notification_channel2"
class NotificationFacade(
    private val context: Context,
    private val notificationRepository: NotificationRepository
) {
init {
    createNotificationChannel(context)
}

    private fun  createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH).apply {
                description =  context.getString(R.string.channel_description)
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    var notificationCount = 0
    fun sendNotification(text: String, title: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.notification_icon))
            .setContentTitle(title)
            .setContentText(text)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationCount, builder.build())
        notificationCount++
        print("notification sent")
    }


    suspend fun listen() {
        while (true) {
            val notifications = notificationRepository.getNewNotification()
            if (notifications != null) {
                for (notification in notifications) {
                    sendNotification(notification.message, "OweSome")
                }
            }

            delay(10.seconds )
        }
    }
}