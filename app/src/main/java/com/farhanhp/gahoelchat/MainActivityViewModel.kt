package com.farhanhp.gahoelchat

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farhanhp.gahoelchat.api.GahoelChatApiService
import com.farhanhp.gahoelchat.api.User

class MainActivityViewModel(
  private val activity: MainActivity
): ViewModel() {
  companion object {
    const val SHARED_PREFERENCE_NAME = "GAHOEL-CHAT-APP"
    const val LOGIN_TOKEN_KEY = "LOGIN_TOKEN"
  }

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
      GahoelChatApiService.logout(loginToken as String, {success()}, {fail()})
    } else {
      success()
    }
  }
}