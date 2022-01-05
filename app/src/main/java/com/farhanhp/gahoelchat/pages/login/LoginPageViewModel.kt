package com.farhanhp.gahoelchat.pages.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhanhp.gahoelchat.services.GahoelChatApiService
import com.farhanhp.gahoelchat.classes.LoginResponse
import com.farhanhp.gahoelchat.isValidEmail
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginPageViewModel : ViewModel() {
  private val _loading = MutableLiveData(false)
  val loading: LiveData<Boolean> get() = _loading

  private val _emailError = MutableLiveData<Boolean>()
  private val _passwordError = MutableLiveData<Boolean>()
  private val _emailMessage = MutableLiveData<String>()
  private val _passwordMessage = MutableLiveData<String>()

  val emailError: LiveData<Boolean> get() = _emailError
  val passwordError: LiveData<Boolean> get() = _passwordError
  val emailMessage: LiveData<String> get() = _emailMessage
  val passwordMessage: LiveData<String> get() = _passwordMessage

  init {
    setProperties()
  }

  private fun setProperties() {
    _emailError.value = false
    _passwordError.value = false
    _emailMessage.value = ""
    _passwordMessage.value = ""
  }

  fun login(email: String, password: String, firebaseRegisterToken: String, loginSuccessCallback: (loginResponse: LoginResponse)->Unit, requestFailureCallback: ()->Unit,) {
    fun loginSuccess(response: Response<LoginResponse>) {
      when(response.code()) {
        200 -> {
          loginSuccessCallback(response.body() as LoginResponse)
        }
        401 -> {
          _passwordError.value = true
          _passwordMessage.value = "Wrong Password"
        }
        404 -> {
          _emailError.value = true
          _emailMessage.value = "Wrong Email"
        }
      }

      _loading.value = false
    }

    fun loginFail() {
      _loading.value = false
      requestFailureCallback()
    }

    setProperties()
    if(email.isNotBlank() && password.isNotBlank()) {
      val isValidEmail = isValidEmail(email)

      if(isValidEmail) {
        _loading.value = true
        viewModelScope.launch {
          GahoelChatApiService.login(email, password, firebaseRegisterToken, {loginSuccess(it)}, {loginFail()})
        }
      } else {
        _emailError.value = true
        _emailMessage.value = "Invalid Email"
      }
    }

    if(email.isBlank()) {
      _emailError.value = true
      _emailMessage.value = "Email must be filled"
    }

    if(password.isBlank()) {
      _passwordError.value = true
      _passwordMessage.value = "Password must be filled"
    }
  }
}