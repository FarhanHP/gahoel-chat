package com.farhanhp.gahoelchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivityViewModelFactory(
  private val activity: MainActivity
): ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
      return MainActivityViewModel(activity) as T
    }

    throw IllegalArgumentException("Unknown ViewModel class")
  }
}