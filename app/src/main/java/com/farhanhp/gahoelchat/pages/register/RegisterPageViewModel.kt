package com.farhanhp.gahoelchat.pages.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farhanhp.gahoelchat.services.GahoelChatApiService
import com.farhanhp.gahoelchat.classes.RegisterResponse
import com.farhanhp.gahoelchat.isValidEmail
import retrofit2.Response

class RegisterPageViewModel(
  private val registerSuccessCallback: ()->Unit,
  private val requestFailureCallback: ()->Unit,
): ViewModel() {
  private val _loading = MutableLiveData(false)
  val loading get() = _loading

  private val _emailError = MutableLiveData<Boolean>()
  private val _emailMessage = MutableLiveData<String>()
  private val _nameError = MutableLiveData<Boolean>()
  private val _nameMessage = MutableLiveData<String>()
  private val _passwordError = MutableLiveData<Boolean>()
  private val _passwordMessage = MutableLiveData<String>()
  private val _repeatPasswordError = MutableLiveData<Boolean>()
  private val _repeatPasswordMessage = MutableLiveData<String>()

  val emailError: LiveData<Boolean> get() = _emailError
  val emailMessage: LiveData<String> get() = _emailMessage
  val nameError: LiveData<Boolean> get() = _nameError
  val nameMessage: LiveData<String> get() = _nameMessage
  val passwordError: LiveData<Boolean> get() = _passwordError
  val passwordMessage: LiveData<String> get() = _passwordMessage
  val repeatPasswordError: LiveData<Boolean> get() = _repeatPasswordError
  val repeatPasswordMessage: LiveData<String> get() = _repeatPasswordMessage

  init {
    setProperties()
  }

  private fun setProperties() {
    _emailError.value = false;
    _emailMessage.value = ""
    _nameError.value = false
    _nameMessage.value = ""
    _passwordError.value = false
    _passwordMessage.value = ""
    _repeatPasswordError.value = false
    _repeatPasswordMessage.value = ""
  }

  fun register(email: String, name: String, password: String, repeatPassword: String) {
    fun registerSuccess(response: Response<RegisterResponse>) {
      val code = response.code()

      if (code == 200) {
        registerSuccessCallback()
      } else if (code == 401) {
        setEmailError("Email Already Exists")
      }

      _loading.value = false
    }

    fun registerFail() {
      _loading.value = false;
      requestFailureCallback()
    }

    setProperties()
    val validEmail = isValidEmail(email)

    if(email.isNotBlank() && validEmail && name.isNotBlank() && password.isNotBlank() && password.length > 8 && repeatPassword.isNotBlank() && repeatPassword == password) {
      _loading.value = true
      GahoelChatApiService.register(email, password, name, {registerSuccess(it)}, {registerFail()})
    }

    if(email.isBlank()) {
      setEmailError("Email must be filled")
    } else if(!validEmail) {
      setEmailError("Invalid Email")
    }

    if(name.isBlank()) {
      setNameError("Name must be filled")
    }

    if(password.isBlank()) {
      setPasswordError("Password must be filled")
    } else if(password.length < 8) {
      setPasswordError("Password must at least 8 characters")
    }

    if(repeatPassword.isBlank()) {
      setRepeatPasswordError("Repeat password must be filled")
    } else if(password != repeatPassword) {
      setRepeatPasswordError("Repeat password must same as password")
    }
  }

  private fun setEmailError(errorMessage: String) {
    _emailError.value = true
    _emailMessage.value = errorMessage
  }

  private fun setNameError(errorMessage: String) {
    _nameError.value = true
    _nameMessage.value = errorMessage
  }

  private fun setPasswordError(errorMessage: String) {
    _passwordError.value = true
    _passwordMessage.value = errorMessage
  }

  private fun setRepeatPasswordError(errorMessage: String) {
    _repeatPasswordError.value = true
    _repeatPasswordMessage.value = errorMessage
  }
}