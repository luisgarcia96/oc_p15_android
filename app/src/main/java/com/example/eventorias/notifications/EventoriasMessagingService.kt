package com.example.eventorias.notifications

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.eventorias.MainActivity
import com.example.eventorias.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class EventoriasMessagingService : FirebaseMessagingService() {
  override fun onNewToken(token: String) {
    Log.d(TAG, "FCM token refreshed: $token")
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    NotificationHelper.createDefaultChannel(this)

    if (!NotificationHelper.canPostNotifications(this)) {
      Log.d(TAG, "Notifications are disabled or permission has not been granted yet.")
      return
    }

    val title = remoteMessage.notification?.title
      ?: remoteMessage.data["title"]
      ?: getString(R.string.notification_default_title)
    val body = remoteMessage.notification?.body
      ?: remoteMessage.data["body"]
      ?: getString(R.string.notification_default_body)

    val contentIntent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      this,
      0,
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(
      this,
      getString(R.string.default_notification_channel_id)
    )
      .setSmallIcon(R.drawable.paper_letter_icon)
      .setContentTitle(title)
      .setContentText(body)
      .setStyle(NotificationCompat.BigTextStyle().bigText(body))
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .build()

    NotificationManagerCompat.from(this).notify(remoteMessage.messageId.hashCodeCompat(), notification)
  }

  private fun String?.hashCodeCompat(): Int {
    return this?.hashCode() ?: (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
  }

  companion object {
    private const val TAG = "EventoriasFCM"
  }
}
