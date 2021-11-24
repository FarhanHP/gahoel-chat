package com.farhanhp.gahoelchat.pages.after_login.chat_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.farhanhp.gahoelchat.api.Room

class ChatRoomPageViewModelFactory(
  private val loginToken: String,
  private val room: Room
): ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(ChatRoomPageViewModel::class.java)) {
      return ChatRoomPageViewModel(loginToken, room) as T
    }

    throw IllegalArgumentException("Unknown ViewModel class")
  }
}