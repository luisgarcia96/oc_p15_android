package com.example.eventorias.events

import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Test

class EventTest {
  @Test
  fun toDocumentMap_containsExpectedFields() {
    val eventAt = Timestamp(1_700_000_000L, 0)
    val createdAt = Timestamp(1_700_000_100L, 0)
    val updatedAt = Timestamp(1_700_000_200L, 0)
    val event = Event(
      id = "event-123",
      title = "Demo",
      description = "Description",
      eventAt = eventAt,
      address = "1 Main St",
      imageUrl = "https://example.com/image.jpg",
      imagePath = "event-images/user/event-123/cover.jpg",
      ownerUserId = "user-1",
      ownerUserEmail = "user@example.com",
      ownerPhotoUrl = "https://example.com/user.jpg",
      createdAt = createdAt,
      updatedAt = updatedAt
    )

    val document = event.toDocumentMap()

    assertEquals(12, document.size)
    assertEquals("event-123", document["id"])
    assertEquals("Demo", document["title"])
    assertEquals("Description", document["description"])
    assertEquals(eventAt, document["eventAt"])
    assertEquals("1 Main St", document["address"])
    assertEquals("https://example.com/image.jpg", document["imageUrl"])
    assertEquals("event-images/user/event-123/cover.jpg", document["imagePath"])
    assertEquals("user-1", document["ownerUserId"])
    assertEquals("user@example.com", document["ownerUserEmail"])
    assertEquals("https://example.com/user.jpg", document["ownerPhotoUrl"])
    assertEquals(createdAt, document["createdAt"])
    assertEquals(updatedAt, document["updatedAt"])
  }
}
