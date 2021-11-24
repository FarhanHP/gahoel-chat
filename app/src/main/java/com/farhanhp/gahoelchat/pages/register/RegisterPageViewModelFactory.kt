package com.farhanhp.gahoelchat.pages.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RegisterPageViewModelFactory(
  private val registerSuccessCallback: ()->Unit,
  private val requestFailureCallback: ()->Unit,
): ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(RegisterPageViewModel::class.java)) {
      return RegisterPageViewModel(registerSuccessCallback, requestFailureCallback) as T
    }

    throw IllegalArgumentException("Unknown ViewModel class")
  }
}