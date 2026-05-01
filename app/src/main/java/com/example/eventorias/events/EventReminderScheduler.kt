package com.example.eventorias.events

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class EventReminderScheduler(
  context: Context,
  private val workManager: WorkManager = WorkManager.getInstance(context)
) {
  fun scheduleReminder(eventId: String, eventTitle: String, eventAtMillis: Long) {
    scheduleSingleReminder(
      uniqueWorkName = EventReminderPolicy.dayBeforeWorkName(eventId),
      eventId = eventId,
      eventTitle = eventTitle,
      eventAtMillis = eventAtMillis,
      notificationType = EventReminderWorker.NOTIFICATION_TYPE_DAY_BEFORE,
      initialDelayMillis = EventReminderPolicy.computeDayBeforeDelayMillis(eventAtMillis)
    )
    scheduleSingleReminder(
      uniqueWorkName = EventReminderPolicy.startTimeWorkName(eventId),
      eventId = eventId,
      eventTitle = eventTitle,
      eventAtMillis = eventAtMillis,
      notificationType = EventReminderWorker.NOTIFICATION_TYPE_START_TIME,
      initialDelayMillis = EventReminderPolicy.computeStartTimeDelayMillis(eventAtMillis)
    )
  }

  fun cancelReminder(eventId: String) {
    workManager.cancelUniqueWork(EventReminderPolicy.dayBeforeWorkName(eventId))
    workManager.cancelUniqueWork(EventReminderPolicy.startTimeWorkName(eventId))
  }

  private fun scheduleSingleReminder(
    uniqueWorkName: String,
    eventId: String,
    eventTitle: String,
    eventAtMillis: Long,
    notificationType: String,
    initialDelayMillis: Long?
  ) {
    val safeInitialDelayMillis = initialDelayMillis ?: return
    val inputData = Data.Builder()
      .putString(EventReminderWorker.KEY_EVENT_ID, eventId)
      .putString(EventReminderWorker.KEY_EVENT_TITLE, eventTitle)
      .putLong(EventReminderWorker.KEY_EVENT_AT_MILLIS, eventAtMillis)
      .putString(EventReminderWorker.KEY_NOTIFICATION_TYPE, notificationType)
      .build()

    val request = OneTimeWorkRequestBuilder<EventReminderWorker>()
      .setInitialDelay(safeInitialDelayMillis, TimeUnit.MILLISECONDS)
      .setInputData(inputData)
      .build()

    workManager.enqueueUniqueWork(
      uniqueWorkName,
      ExistingWorkPolicy.REPLACE,
      request
    )
  }
}
