package com.farhanhp.gahoelchat.classes

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallbackWrapper<T>(
  private val successCallback: (response: Response<T>)->Unit,
  private val failureCallback: ()->Unit
): Callback<T> {
  override fun onFailure(call: Call<T>, t: Throwable) {
    Log.e("retrofit", t.toString())

    failureCallback()
  }

  override fun onResponse(call: Call<T>, response: Response<T>) {
    successCallback(response)
  }
}