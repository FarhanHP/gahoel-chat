package com.farhanhp.gahoelchat.pages.splash_screen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhanhp.gahoelchat.classes.GetProfileResponse
import com.farhanhp.gahoelchat.classes.User
import com.farhanhp.gahoelchat.services.GahoelChatApiService
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import retrofit2.Response
import com.google.android.gms.tasks.OnCompleteListener

class SplashScreenPageViewModel(
  redirectToLoginPage: ()->Unit,
  redirectToHomePage: ()->Unit,
  loginToken: String?,
): ViewModel() {
  private val _loadingText = MutableLiveData<String>()
  private val _firebaseRegisterToken = MutableLiveData<String>()
  private val _loginToken = MutableLiveData(loginToken)
  private val _loginUser = MutableLiveData<User?>()

  val loadingText: LiveData<String>
    get() = _loadingText
  val firebaseRegisterToken: LiveData<String>
    get() = _firebaseRegisterToken
  val loginToken: LiveData<String?>
    get() = _loginToken
  val loginUser: LiveData<User?>
    get() = _loginUser

  init {
    setLoadingText("Acquiring firebase register token...")
    setFirebaseRegisterToken({
      _firebaseRegisterToken.value = it
      setLoadingText("Checking login status...")
      checkLoginStatus({
        setLoadingText("Registering firebase register token to server")
        registerFirebaseRegistrationToken({
          redirectToHomePage()
        }, {
          setLoadingText("Fail to register firebase register token to server. Cannot enter app.")
        })
      }, {
        redirectToLoginPage()
      })
    }) {
      setLoadingText("Fail to acquire firebase register token. Cannot enter app.")
    }
  }

  private fun setLoadingText(text: String) {
    _loadingText.value = text
  }

  private fun setFirebaseRegisterToken(
    onSuccessCallback: (token: String)->Unit,
    onFailCallback: ()->Unit
  ) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
      if (!task.isSuccessful) {
        Log.w("FirebaseMessaging", "Fetching FCM registration token failed", task.exception)
        onFailCallback()
        return@OnCompleteListener
      }

      onSuccessCallback(task.result as String)
    })
  }

  private fun checkLoginStatus(
    onSuccessCallback: () -> Unit,
    onFailCallback: () -> Unit,
  ) {
    fun success(response: Response<GetProfileResponse>) {
      val code = response.code()

      if (code == 200) {
        _loginUser.value = response.body()?.content
        onSuccessCallback()
      } else {
        _loginToken.value = null
        onFailCallback()
      }
    }

    val loginToken = _loginToken.value
    if(loginToken != null) {
      viewModelScope.launch {
        GahoelChatApiService.getProfile(loginToken, {success(it)}, {onFailCallback()})
      }
    } else {
      onFailCallback()
    }
  }

  private fun registerFirebaseRegistrationToken(
    onSuccessCallback: () -> Unit,
    onFailCallback: () -> Unit,
  ) {
    val loginToken = _loginToken.value
    if(loginToken != null) {
      viewModelScope.launch {
        GahoelChatApiService.registerFirebaseRegistrationToken(loginToken, {onSuccessCallback()}, {onFailCallback()})
      }

    } else {
      onFailCallback()
    }
  }
}