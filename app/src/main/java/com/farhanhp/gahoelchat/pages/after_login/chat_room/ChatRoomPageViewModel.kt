package com.farhanhp.gahoelchat.pages.after_login.chat_room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farhanhp.gahoelchat.classes.CreateMessageResponse
import com.farhanhp.gahoelchat.classes.Message
import com.farhanhp.gahoelchat.classes.Room
import com.farhanhp.gahoelchat.classes.User
import com.farhanhp.gahoelchat.services.GahoelChatApiService
import retrofit2.Response
import java.util.*

class ChatRoomPageViewModel(
  private val loginUser: User,
  private val loginToken: String,
  private val room: Room
): ViewModel() {
  private val tempMessageIds = mutableListOf<String>()
  private val _messages = MutableLiveData(room.messages)
  val messages: LiveData<List<Message>> get() = _messages

  fun createMessage(messageBody: String,  failCallback: ()->Unit) {
    fun fail(tempMessageId: String) {
      removeMessageById(tempMessageId)
      failCallback()
    }

    val tempMessageId = Date().time.toString()
    tempMessageIds.add(0, tempMessageId)
    val tempMessage = Message(tempMessageId, messageBody, true, -1L)
    insertMessage(tempMessage)
    GahoelChatApiService.createMessage(loginToken, messageBody, room._id, {}, {fail(tempMessageId)})
  }

  fun insertMessageByFCM(senderUserId: String, messageId: String, messageBody: String, createdAt: Long) {
    val isOwner = loginUser._id == senderUserId
    if(isOwner && tempMessageIds.size > 0) {
      removeMessageById(tempMessageIds.removeAt(0))
    }

    insertMessage(Message(messageId, messageBody, isOwner, createdAt))
  }

  private fun insertMessage(newMessage: Message) {
    val newMessages = (_messages.value as List).toMutableList()
    newMessages.add(0, newMessage)
    _messages.value = newMessages
  }

  private fun removeMessageById(messageId: String) {
    _messages.value = getExcludedMessages(messageId)
  }

  private fun getExcludedMessages(messageId: String): List<Message> {
    return (_messages.value as List).filter {
      it._id != messageId
    }
  }
}