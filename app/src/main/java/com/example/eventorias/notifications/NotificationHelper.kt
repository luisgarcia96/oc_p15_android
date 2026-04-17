package com.example.eventorias.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.eventorias.R

object NotificationHelper {
  fun createDefaultChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val notificationManager = context.getSystemService(NotificationManager::class.java) ?: return
    val channel = NotificationChannel(
      context.getString(R.string.default_notification_channel_id),
      context.getString(R.string.notification_channel_name),
      NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
      description = context.getString(R.string.notification_channel_description)
    }
    notificationManager.createNotificationChannel(channel)
  }

  fun hasPostNotificationsPermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
  }

  fun canPostNotifications(context: Context): Boolean {
    return hasPostNotificationsPermission(context) &&
      NotificationManagerCompat.from(context).areNotificationsEnabled()
  }
}
