package com.farhanhp.gahoelchat.services.firebase

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class GahoelFirebaseMessagingService: FirebaseMessagingService() {
  companion object {
    val MESSAGE_KEYS = listOf("type", "_id", "roomId", "messageBody", "senderUserId", "createdAt")
    val ROOM_KEYS = listOf("type", "senderUserName", "senderUserId", "senderImage", "_id", "lastInteractAt", "createdAt", "updatedAt", "firstMessageBody", "firstMessageId")
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    val data = remoteMessage.data

    if (data.isNotEmpty()) {
      val intent = Intent()

      (when(data["type"]){
        "message" -> MESSAGE_KEYS
        else -> ROOM_KEYS
      }).forEach{
        intent.putExtra(it, data[it])
      }
      intent.action = "receive_message"
      sendBroadcast(intent)
    }
  }

  override fun onNewToken(token: String) {
    Log.d("12345", "Refreshed token: $token")
  }
}