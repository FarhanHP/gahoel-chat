package com.farhanhp.gahoelchat.pages.after_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AfterLoginPageViewModelFactory(
  private val loginToken: String,
  private val fetchRoomsSuccessCallback: () -> Unit,
  private val fetchRoomsFailCallback: () -> Unit
): ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(AfterLoginPageViewModel::class.java)) {
      return AfterLoginPageViewModel(loginToken, fetchRoomsSuccessCallback, fetchRoomsFailCallback) as T
    }

    throw IllegalArgumentException("Unknown ViewModel class")
  }
}