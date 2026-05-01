package com.example.eventorias.events

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eventorias.MainActivity
import com.example.eventorias.R
import com.example.eventorias.notifications.NotificationHelper

class EventReminderWorker(
  appContext: Context,
  workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
  override suspend fun doWork(): Result {
    val eventId = inputData.getString(KEY_EVENT_ID) ?: return Result.failure()
    val eventTitle = inputData.getString(KEY_EVENT_TITLE) ?: return Result.failure()
    val eventAtMillis = inputData.getLong(KEY_EVENT_AT_MILLIS, -1L)
    val notificationType = inputData.getString(KEY_NOTIFICATION_TYPE) ?: return Result.failure()
    if (eventAtMillis <= 0L) return Result.failure()

    NotificationHelper.createDefaultChannel(applicationContext)
    if (!NotificationHelper.canPostNotifications(applicationContext)) {
      return Result.success()
    }

    val pendingIntent = PendingIntent.getActivity(
      applicationContext,
      notificationId(eventId, notificationType),
      Intent(applicationContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(
      applicationContext,
      applicationContext.getString(R.string.default_notification_channel_id)
    )
      .setSmallIcon(R.drawable.paper_letter_icon)
      .setContentTitle(titleForType(notificationType))
      .setContentText(bodyForType(notificationType, eventTitle))
      .setStyle(
        NotificationCompat.BigTextStyle().bigText(
          bodyForType(notificationType, eventTitle)
        )
      )
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .build()

    NotificationManagerCompat.from(applicationContext)
      .notify(notificationId(eventId, notificationType), notification)
    return Result.success()
  }

  private fun titleForType(notificationType: String): String = when (notificationType) {
    NOTIFICATION_TYPE_DAY_BEFORE -> applicationContext.getString(R.string.event_reminder_title)
    NOTIFICATION_TYPE_START_TIME -> applicationContext.getString(R.string.event_start_title)
    else -> applicationContext.getString(R.string.event_reminder_title)
  }

  private fun bodyForType(notificationType: String, eventTitle: String): String = when (notificationType) {
    NOTIFICATION_TYPE_DAY_BEFORE -> applicationContext.getString(R.string.event_reminder_body, eventTitle)
    NOTIFICATION_TYPE_START_TIME -> applicationContext.getString(R.string.event_start_body, eventTitle)
    else -> applicationContext.getString(R.string.event_reminder_body, eventTitle)
  }

  private fun notificationId(eventId: String, notificationType: String): Int {
    return "$eventId:$notificationType".hashCode()
  }

  companion object {
    const val KEY_EVENT_ID = "event_id"
    const val KEY_EVENT_TITLE = "event_title"
    const val KEY_EVENT_AT_MILLIS = "event_at_millis"
    const val KEY_NOTIFICATION_TYPE = "notification_type"

    const val NOTIFICATION_TYPE_DAY_BEFORE = "day_before"
    const val NOTIFICATION_TYPE_START_TIME = "start_time"
  }
}
