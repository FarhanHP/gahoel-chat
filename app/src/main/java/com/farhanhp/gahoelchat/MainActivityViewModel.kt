package com.farhanhp.gahoelchat

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhanhp.gahoelchat.services.GahoelChatApiService
import com.farhanhp.gahoelchat.classes.User
import kotlinx.coroutines.launch

class MainActivityViewModel(
  private val activity: MainActivity
): ViewModel() {
  companion object {
    const val SHARED_PREFERENCE_NAME = "GAHOEL-CHAT-APP"
    const val LOGIN_TOKEN_KEY = "LOGIN_TOKEN"
  }
  private val onPassNewMessageFromFCMListeners: MutableList<(
    roomId: String,
    senderUserId: String,
    messageId: String,
    messageBody: String,
    createdAt: Long
  ) -> Unit> = mutableListOf()

  private val onPassNewRoomFromFCMListeners: MutableList<(
    senderUserName: String,
    senderUserId: String,
    senderImage: String,
    roomId: String,
    lastInteractAt: Long,
    createdAt: Long,
    updatedAt: Long,
    firstMessageBody: String,
    firstMessageId: String
  ) -> Unit> = mutableListOf()

  var firebaseRegisterToken: String? = null
  var loginUser: User? = null
  var loginToken: String? = null
    get() {
      if(field == null) {
        field = activity.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString(LOGIN_TOKEN_KEY, null)
      }
      return field
    }
    set(value) {
      activity.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putString(LOGIN_TOKEN_KEY, value).apply()
      field = value
    }

  private val _loggingOut = MutableLiveData(false)
  val loggingOut: LiveData<Boolean> get() = _loggingOut

  fun passNewMessageFromFCM(
    roomId: String,
    senderUserId: String,
    messageId: String,
    messageBody: String,
    createdAt: Long
  ) {
    onPassNewMessageFromFCMListeners.forEach{
      it(roomId, senderUserId, messageId, messageBody, createdAt)
    }
  }

  fun passNewRoomFromFCM(
    senderUserName: String,
    senderUserId: String,
    senderImage: String,
    roomId: String,
    lastInteractAt: Long,
    createdAt: Long,
    updatedAt: Long,
    firstMessageBody: String,
    firstMessageId: String,
  ) {
    onPassNewRoomFromFCMListeners.forEach{
      it(senderUserName, senderUserId, senderImage, roomId, lastInteractAt, createdAt, updatedAt, firstMessageBody, firstMessageId)
    }
  }

  fun addOnPassNewMessageFromFCMListener(fn: (
    roomId: String,
    senderUserId: String,
    messageId: String,
    messageBody: String,
    createdAt: Long
  ) -> Unit) {
    onPassNewMessageFromFCMListeners.add(fn)
  }

  fun addOnPassNewRoomFromFCMListener(fn: (
    senderUserName: String,
    senderUserId: String,
    senderImage: String,
    roomId: String,
    lastInteractAt: Long,
    createdAt: Long,
    updatedAt: Long,
    firstMessageBody: String,
    firstMessageId: String
  ) -> Unit) {
    onPassNewRoomFromFCMListeners.add(fn)
  }

  fun logout(successCallback: () -> Unit, failureCallback: () -> Unit) {
    fun success() {
      _loggingOut.value = false
      loginUser = null
      loginToken = null
      successCallback()
    }

    fun fail() {
      _loggingOut.value = false
      failureCallback()
    }

    if(loginToken != null) {
      _loggingOut.value = true
      viewModelScope.launch {
        GahoelChatApiService.logout(loginToken as String, {success()}, {fail()})
      }
    } else {
      success()
    }
  }
}