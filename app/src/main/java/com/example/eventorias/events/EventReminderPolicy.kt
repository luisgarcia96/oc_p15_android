package com.example.eventorias.events

import java.time.Duration

object EventReminderPolicy {
  private val DAY_BEFORE_OFFSET: Duration = Duration.ofHours(24)
  private const val DAY_BEFORE_WORK_NAME_PREFIX = "event-reminder:day-before:"
  private const val START_TIME_WORK_NAME_PREFIX = "event-reminder:start-time:"

  fun dayBeforeWorkName(eventId: String): String = "$DAY_BEFORE_WORK_NAME_PREFIX$eventId"

  fun startTimeWorkName(eventId: String): String = "$START_TIME_WORK_NAME_PREFIX$eventId"

  fun computeDayBeforeDelayMillis(
    eventAtMillis: Long,
    nowMillis: Long = System.currentTimeMillis()
  ): Long? {
    val reminderAtMillis = eventAtMillis - DAY_BEFORE_OFFSET.toMillis()
    return computeDelayMillis(reminderAtMillis, nowMillis)
  }

  fun computeStartTimeDelayMillis(
    eventAtMillis: Long,
    nowMillis: Long = System.currentTimeMillis()
  ): Long? = computeDelayMillis(eventAtMillis, nowMillis)

  private fun computeDelayMillis(targetAtMillis: Long, nowMillis: Long): Long? {
    if (targetAtMillis < nowMillis) return null
    return targetAtMillis - nowMillis
  }
}
