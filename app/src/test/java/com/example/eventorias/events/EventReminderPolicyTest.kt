package com.example.eventorias.events

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EventReminderPolicyTest {
  @Test
  fun computeDayBeforeDelayMillis_returnsDelayWhenEventIsMoreThan24HoursAway() {
    val nowMillis = 1_000L
    val eventAtMillis = nowMillis + 30L * 60L * 60L * 1000L

    val delay = EventReminderPolicy.computeDayBeforeDelayMillis(
      eventAtMillis = eventAtMillis,
      nowMillis = nowMillis
    )

    assertEquals(6L * 60L * 60L * 1000L, delay)
  }

  @Test
  fun computeDayBeforeDelayMillis_returnsZeroWhenEventIsExactly24HoursAway() {
    val nowMillis = 1_000L
    val eventAtMillis = nowMillis + 24L * 60L * 60L * 1000L

    val delay = EventReminderPolicy.computeDayBeforeDelayMillis(
      eventAtMillis = eventAtMillis,
      nowMillis = nowMillis
    )

    assertEquals(0L, delay)
  }

  @Test
  fun computeDayBeforeDelayMillis_returnsNullWhenReminderTimeIsAlreadyPast() {
    val nowMillis = 1_000L
    val eventAtMillis = nowMillis + 23L * 60L * 60L * 1000L

    val delay = EventReminderPolicy.computeDayBeforeDelayMillis(
      eventAtMillis = eventAtMillis,
      nowMillis = nowMillis
    )

    assertNull(delay)
  }

  @Test
  fun computeStartTimeDelayMillis_returnsDelayWhenEventIsInFuture() {
    val nowMillis = 1_000L
    val eventAtMillis = nowMillis + 2L * 60L * 60L * 1000L

    val delay = EventReminderPolicy.computeStartTimeDelayMillis(
      eventAtMillis = eventAtMillis,
      nowMillis = nowMillis
    )

    assertEquals(2L * 60L * 60L * 1000L, delay)
  }

  @Test
  fun computeStartTimeDelayMillis_returnsNullWhenEventIsInPast() {
    val nowMillis = 2_000L
    val eventAtMillis = 1_000L

    val delay = EventReminderPolicy.computeStartTimeDelayMillis(
      eventAtMillis = eventAtMillis,
      nowMillis = nowMillis
    )

    assertNull(delay)
  }

  @Test
  fun workNames_areStablePerEventId() {
    assertEquals(
      "event-reminder:day-before:event-42",
      EventReminderPolicy.dayBeforeWorkName("event-42")
    )
    assertEquals(
      "event-reminder:start-time:event-42",
      EventReminderPolicy.startTimeWorkName("event-42")
    )
  }
}
