package com.farhanhp.gahoelchat.pages.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.Error

class SplashScreenPageViewModelFactory(
  private val redirectToLoginPage: () -> Unit,
  private val redirectToHomePage: ()->Unit,
  private val loginToken: String?
): ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(SplashScreenPageViewModel::class.java)) {
      return SplashScreenPageViewModel(redirectToLoginPage, redirectToHomePage, loginToken) as T
    } else {
      throw Error("Unknown ViewModel class")
    }
  }
}