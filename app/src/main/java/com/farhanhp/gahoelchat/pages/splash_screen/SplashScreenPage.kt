package com.farhanhp.gahoelchat.pages.splash_screen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.farhanhp.gahoelchat.MainActivity
import com.farhanhp.gahoelchat.MainActivityViewModel
import com.farhanhp.gahoelchat.MainActivityViewModelFactory
import com.farhanhp.gahoelchat.R
import com.farhanhp.gahoelchat.services.GahoelChatApiService
import com.farhanhp.gahoelchat.classes.GetProfileResponse
import com.farhanhp.gahoelchat.databinding.PageSplashScreenBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Response

class SplashScreenPage : Fragment() {
  private lateinit var binding: PageSplashScreenBinding
  private lateinit var mainActivityModel: MainActivityViewModel
  private lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
  private lateinit var loadingTextView: TextView

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DataBindingUtil.inflate(layoutInflater, R.layout.page_splash_screen, container, false)
    loadingTextView = binding.loadingText
    mainActivityViewModelFactory = MainActivityViewModelFactory(requireActivity() as MainActivity)
    mainActivityModel = ViewModelProvider(requireActivity(), mainActivityViewModelFactory).get(MainActivityViewModel::class.java)

    setLoadingTextViewText("Acquiring firebase register token...")
    setFirebaseRegisterToken({
      mainActivityModel.firebaseRegisterToken = it
      setLoadingTextViewText("Checking login status...")
      checkLoginStatus({
        setLoadingTextViewText("Registering firebase register token to server")
        registerFirebaseRegistrationToken({
          redirectToHomePage()
        }, {
          setLoadingTextViewText("Fail to register firebase register token to server. Cannot enter app.")
        })
      }, {
        redirectToLoginPage()
      })
    }) {
      setLoadingTextViewText("Fail to acquire firebase register token. Cannot enter app.")
    }

    return binding.root
  }

  private fun setLoadingTextViewText(text: String) {
    loadingTextView.text = text
  }

  private fun redirectToLoginPage() {
    findNavController().navigate(SplashScreenPageDirections.actionSplashScreenPageToLoginPage())
  }

  private fun redirectToHomePage() {
    findNavController().navigate(SplashScreenPageDirections.actionSplashScreenPageToAfterLoginPage())
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
        mainActivityModel.loginUser = response.body()?.content
        onSuccessCallback()
      } else {
        mainActivityModel.loginToken = null
        onFailCallback()
      }
    }

    val loginToken = mainActivityModel.loginToken

    if(loginToken != null) {
      GahoelChatApiService.getProfile(loginToken, {success(it)}, {onFailCallback()})
    } else {
      onFailCallback()
    }
  }

  private fun registerFirebaseRegistrationToken(
    onSuccessCallback: () -> Unit,
    onFailCallback: () -> Unit,
  ) {
    val loginToken = mainActivityModel.loginToken
    if(loginToken != null) {
      GahoelChatApiService.registerFirebaseRegistrationToken(loginToken, {onSuccessCallback()}, {onFailCallback()})
    } else {
      onFailCallback()
    }
  }
}