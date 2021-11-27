package com.farhanhp.gahoelchat.pages.after_login.chat_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.farhanhp.gahoelchat.classes.Room
import com.farhanhp.gahoelchat.classes.User

class ChatRoomPageViewModelFactory(
  private val loginUser: User,
  private val loginToken: String,
  private val room: Room
): ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(ChatRoomPageViewModel::class.java)) {
      return ChatRoomPageViewModel(loginUser, loginToken, room) as T
    }

    throw IllegalArgumentException("Unknown ViewModel class")
  }
}