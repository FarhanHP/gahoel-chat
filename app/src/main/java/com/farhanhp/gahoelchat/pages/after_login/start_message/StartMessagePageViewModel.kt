package com.farhanhp.gahoelchat.pages.after_login.start_message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farhanhp.gahoelchat.classes.CreateNewMessageResponse
import com.farhanhp.gahoelchat.services.GahoelChatApiService
import com.farhanhp.gahoelchat.classes.Room
import com.farhanhp.gahoelchat.isValidEmail
import retrofit2.Response

class StartMessagePageViewModel(
  private val loginToken: String
): ViewModel() {
  private val _emailError = MutableLiveData<Boolean>()
  private val _emailMessage = MutableLiveData<String>()
  private val _messageBodyError = MutableLiveData<Boolean>()
  private val _messageBodyMessage = MutableLiveData<String>()

  val emailError: LiveData<Boolean> get() = _emailError
  val emailMessage: LiveData<String> get() = _emailMessage
  val messageBodyError: LiveData<Boolean> get() = _messageBodyError
  val messageBodyMessage: LiveData<String> get() = _messageBodyMessage

  private val _loading = MutableLiveData(false)
  val loading: LiveData<Boolean> get() = _loading

  init {
    setProperties()
  }

  fun createNewMessage(recipientEmail: String, messageBody: String, successCallback: (room: Room)->Unit, failureCallback: ()->Unit) {
    fun success(response: Response<CreateNewMessageResponse>) {
      _loading.value = false
      when(response.code()) {
        200 -> {
          val body = response.body()
          if(body != null) {
            successCallback(body.content)
          }
        } 401 -> {
          setEmailError("Cannot send message to yourself")
        } else -> {
          setEmailError("Email doesn't exist")
        }
      }
    }

    fun fail() {
      _loading.value = false
      failureCallback()
    }

    setProperties()
    val validEmail = isValidEmail(recipientEmail)
    if(recipientEmail.isNotBlank() && messageBody.isNotBlank() && validEmail){
      _loading.value = true
      GahoelChatApiService.createNewMessage(loginToken, recipientEmail, messageBody, {success(it)}, {fail()})
    }

    if(recipientEmail.isBlank()) {
      setEmailError("Email cannot blank")
    } else if(!validEmail) {
      setEmailError("Invalid email")
    }

    if(messageBody.isBlank()) {
      setMessageBodyError("Message body cannot blank")
    }
  }

  private fun setEmailError(errorMessage: String) {
    _emailError.value = true
    _emailMessage.value = errorMessage
  }

  private fun setMessageBodyError(errorMessage: String) {
    _messageBodyError.value = true
    _messageBodyMessage.value = errorMessage
  }

  private fun setProperties() {
    _emailError.value = false
    _emailMessage.value = ""
    _messageBodyError.value = false
    _messageBodyMessage.value = ""
  }
}