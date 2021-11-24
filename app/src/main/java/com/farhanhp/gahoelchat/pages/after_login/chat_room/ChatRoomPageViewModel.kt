package com.farhanhp.gahoelchat.pages.after_login.chat_room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farhanhp.gahoelchat.api.*
import retrofit2.Response
import java.util.*

class ChatRoomPageViewModel(
  private val loginToken: String,
  private val room: Room
): ViewModel() {
  private val _messages = MutableLiveData(room.messages)
  val messages: LiveData<List<Message>> get() = _messages

  fun createMessage(messageBody: String, successCallback: (message: Message)->Unit, failCallback: ()->Unit) {
    fun success(response: Response<CreateMessageResponse>, tempMessageId: String) {
      when(response.code()) {
        200 -> {
          val body = response.body()
          if(body != null) {
            val newMessage = body.content.message
            val newMessages = getExcludedMessages(tempMessageId).toMutableList()
            newMessages.add(0, newMessage)
            _messages.value = newMessages
            successCallback(newMessage)
          }
        }
        else -> {
          failCallback()
        }
      }
    }

    fun fail(tempMessageId: String) {
      removeMessageById(tempMessageId)
      failCallback()
    }

    val tempMessageId = Date().time.toString()
    val tempMessage = Message(tempMessageId, messageBody, true, -1L)
    insertMessage(tempMessage)
    GahoelChatApiService.createMessage(loginToken, messageBody, room._id, {success(it, tempMessageId)}, {fail(tempMessageId)})
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