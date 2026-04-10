package com.example.eventorias.events

import android.content.Context
import android.net.Uri
import com.example.eventorias.auth.awaitResult
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EventsRepository(
  private val appContext: Context,
  private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
  private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
  private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
  suspend fun createEvent(draft: EventDraft): String {
    val currentUser = requireNotNull(auth.currentUser) {
      "A signed-in user is required to create an event."
    }
    val imageUri = requireNotNull(draft.imageUri) {
      "An event image is required."
    }

    val eventId = firestore.collection(EVENTS_COLLECTION).document().id
    val imagePath = "$EVENT_IMAGES_PATH/${currentUser.uid}/$eventId/cover.jpg"
    val imageReference = storage.reference.child(imagePath)
    val imageUrl = uploadImage(imageReference, imageUri)
    val now = Timestamp.now()

    val event = Event(
      id = eventId,
      title = draft.title.trim(),
      description = draft.description.trim(),
      eventAt = parseEventAt(draft.date, draft.time),
      address = draft.address.trim(),
      imageUrl = imageUrl,
      imagePath = imagePath,
      ownerUserId = currentUser.uid,
      ownerUserEmail = currentUser.email,
      ownerPhotoUrl = currentUser.photoUrl?.toString(),
      createdAt = now,
      updatedAt = now
    )

    return try {
      firestore.collection(EVENTS_COLLECTION)
        .document(eventId)
        .set(event.toDocumentMap())
        .awaitResult()
      eventId
    } catch (throwable: Throwable) {
      runCatching { imageReference.delete().awaitResult() }
      throw throwable
    }
  }

  suspend fun deleteEvent(eventId: String, imagePath: String) {
    firestore.collection(EVENTS_COLLECTION).document(eventId).delete().awaitResult()
    runCatching { storage.reference.child(imagePath).delete().awaitResult() }
  }

  fun getEvents(): Flow<List<Event>> = callbackFlow {
    val listener = firestore.collection(EVENTS_COLLECTION)
      .orderBy("eventAt", Query.Direction.DESCENDING)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          close(error)
          return@addSnapshotListener
        }
        val events = snapshot?.documents?.mapNotNull { it.toObject(Event::class.java) } ?: emptyList()
        trySend(events)
      }
    awaitClose { listener.remove() }
  }

  private suspend fun uploadImage(reference: StorageReference, imageUri: Uri): String {
    val contentType = appContext.contentResolver.getType(imageUri) ?: DEFAULT_IMAGE_CONTENT_TYPE
    val metadata = StorageMetadata.Builder()
      .setContentType(contentType)
      .build()

    val taskSnapshot = reference.putFile(imageUri, metadata).awaitResult()
    val uploadedReference = taskSnapshot.metadata?.reference ?: reference
    return uploadedReference.downloadUrl.awaitResult().toString()
  }

  private fun parseEventAt(date: String, time: String): Timestamp {
    val parsedDate = LocalDate.parse(date.trim(), DATE_FORMATTER)
    val parsedTime = LocalTime.parse(time.trim().replace(" ", ""), TIME_FORMATTER)
    val instant = parsedDate
      .atTime(parsedTime)
      .atZone(ZoneId.systemDefault())
      .toInstant()
    return Timestamp(instant.epochSecond, instant.nano)
  }

  companion object {
    private const val EVENTS_COLLECTION = "events"
    private const val EVENT_IMAGES_PATH = "event-images"
    private const val DEFAULT_IMAGE_CONTENT_TYPE = "image/jpeg"
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/uuuu")
    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
  }
}
