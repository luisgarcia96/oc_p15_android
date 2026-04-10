package com.example.eventorias.events

import android.net.Uri
import com.google.firebase.Timestamp

data class Event(
  val id: String = "",
  val title: String = "",
  val description: String = "",
  val eventAt: Timestamp = Timestamp.now(),
  val address: String = "",
  val imageUrl: String = "",
  val imagePath: String = "",
  val ownerUserId: String = "",
  val ownerUserEmail: String? = null,
  val ownerPhotoUrl: String? = null,
  val createdAt: Timestamp = Timestamp.now(),
  val updatedAt: Timestamp = Timestamp.now()
) {
  fun toDocumentMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "title" to title,
    "description" to description,
    "eventAt" to eventAt,
    "address" to address,
    "imageUrl" to imageUrl,
    "imagePath" to imagePath,
    "ownerUserId" to ownerUserId,
    "ownerUserEmail" to ownerUserEmail,
    "ownerPhotoUrl" to ownerPhotoUrl,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
  )
}

data class EventDraft(
  val title: String = "",
  val description: String = "",
  val date: String = "",
  val time: String = "",
  val address: String = "",
  val imageUri: Uri? = null
)
