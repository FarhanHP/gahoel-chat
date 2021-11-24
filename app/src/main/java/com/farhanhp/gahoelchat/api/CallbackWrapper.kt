package com.farhanhp.gahoelchat.api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class CallbackWrapper<T>(
  private val successCallback: (response: Response<T>)->Unit,
  private val failureCallback: ()->Unit
): Callback<T> {
  override fun onFailure(call: Call<T>, t: Throwable) {
    Log.e("retrofit", t.toString())

    // heroku server usually off automatically after 30 minutes idle
    if(t is SocketTimeoutException) {
      // TODO add handler
    }

    failureCallback()
  }

  override fun onResponse(call: Call<T>, response: Response<T>) {
    successCallback(response)
  }
}