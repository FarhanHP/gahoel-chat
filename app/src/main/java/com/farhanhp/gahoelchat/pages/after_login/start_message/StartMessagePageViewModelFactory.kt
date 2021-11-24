package com.farhanhp.gahoelchat.pages.after_login.start_message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StartMessagePageViewModelFactory(
  private val loginToken: String
): ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(StartMessagePageViewModel::class.java)) {
      return StartMessagePageViewModel(loginToken) as T
    }

    throw IllegalArgumentException("Unknown ViewModel class")
  }
}