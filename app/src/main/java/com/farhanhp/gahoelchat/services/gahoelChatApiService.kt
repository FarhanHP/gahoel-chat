package com.farhanhp.gahoelchat.services

import android.util.Log
import com.farhanhp.gahoelchat.classes.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.net.SocketTimeoutException

private const val BASE_URL = "https://gahoel-chat-api.herokuapp.com/api/"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
private val retrofit = Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi)).baseUrl(
  BASE_URL
).build()

interface GahoelChatApi {
  @POST("user/login")
  fun login(@Body loginBody: LoginBody): Call<LoginResponse>

  @POST("user/register")
  fun register(@Body registerBody: RegisterBody): Call<RegisterResponse>

  @POST("after-login/message/create/new")
  fun createNewMessage(@Header("login_token") loginToken: String, @Body createNewMessageBody: CreateNewMessageBody): Call<CreateNewMessageResponse>

  @POST("after-login/message/create")
  fun createMessage(@Header("login_token") loginToken: String, @Body createMessageBody: CreateMessageBody): Call<CreateMessageResponse>

  @POST("after-login/user/firebase-registration-token")
  fun registerFirebaseRegistrationToken(@Header("login_token") loginToken: String): Call<RegisterFirebaseRegistrationTokenResponse>

  @GET("after-login/user/profile")
  fun getProfile(@Header("login_token") loginToken: String): Call<GetProfileResponse>

  @GET("after-login/room")
  fun getRooms(@Header("login_token") loginToken: String): Call<GetRoomsResponse>

  @DELETE("after-login/user/logout")
  fun logout(@Header("login_token") loginToken: String): Call<LogoutResponse>
}

object GahoelChatApiService {
  private val retrofitService: GahoelChatApi by lazy {
    retrofit.create(GahoelChatApi::class.java)
  }

  suspend fun login(
    email: String,
    password: String,
    firebaseRegisterToken: String,
    successCallback: (response: Response<LoginResponse>)->Unit,
    failureCallback: ()->Unit,
    timeoutCount: Int = 0
  ) {
    try {
      val response = retrofitService.login(LoginBody(email, password, firebaseRegisterToken)).awaitResponse()
      successCallback(response)
      return
    } catch (error: Throwable) {
      Log.e(TAG, error.message.toString())

      // resend request when heroku server is down due to idling for certain time
      if(error is SocketTimeoutException && timeoutCount < 2) {
        return login(email, password, firebaseRegisterToken, successCallback, failureCallback, timeoutCount + 1)
      }
    }

    failureCallback()
  }

  suspend fun register(
    email: String,
    password: String,
    name: String,
    successCallback: (response: Response<RegisterResponse>)->Unit,
    failureCallback: ()->Unit,
    timeoutCount: Int = 0
  ) {
    try {
      val response = retrofitService.register(RegisterBody(email, password, name)).awaitResponse()
      successCallback(response)
      return
    } catch (error: Throwable) {
      Log.e(TAG, error.message.toString())

      if(error is SocketTimeoutException && timeoutCount < 2) {
        return register(email, password, name, successCallback, failureCallback, timeoutCount + 1)
      }
    }

    failureCallback()
  }

  suspend fun getProfile(
    loginToken: String,
    successCallback: (response: Response<GetProfileResponse>) -> Unit,
    failureCallback: () -> Unit,
    timeoutCount: Int = 0
  ) {
    try {
      val response = retrofitService.getProfile(loginToken).awaitResponse()
      successCallback(response)
      return
    } catch (error: Throwable) {
      if(error is SocketTimeoutException && timeoutCount < 2) {
        return getProfile(loginToken, successCallback, failureCallback, timeoutCount + 1)
      }
    }

    failureCallback()
  }

  suspend fun getRooms(
    loginToken: String,
    successCallback: (response: Response<GetRoomsResponse>) -> Unit,
    failureCallback: () -> Unit,
    timeoutCount: Int = 0
  ) {
    try {
      val response = retrofitService.getRooms(loginToken).awaitResponse()
      successCallback(response)
      return
    } catch (error: Throwable) {
      if(error is SocketTimeoutException && timeoutCount < 2) {
        return getRooms(loginToken, successCallback, failureCallback, timeoutCount + 1)
      }
    }

    failureCallback()
  }

  suspend fun createNewMessage(
    loginToken: String,
    recipientEmail: String,
    messageBody: String,
    successCallback: (response: Response<CreateNewMessageResponse>) -> Unit,
    failureCallback: () -> Unit,
    timeoutCount: Int = 0
  ) {
    try {
      val response = retrofitService.createNewMessage(loginToken, CreateNewMessageBody(recipientEmail, messageBody)).awaitResponse()
      successCallback(response)
      return
    } catch (error: Throwable) {
      if(error is SocketTimeoutException && timeoutCount < 2) {
        return createNewMessage(loginToken, recipientEmail, messageBody, successCallback, failureCallback, timeoutCount + 1)
      }
    }

    failureCallback()
  }

  suspend fun createMessage(
    loginToken: String,
    messageBody: String,
    roomId: String,
    successCallback: (response: Response<CreateMessageResponse>) -> Unit,
    failureCallback: () -> Unit,
    timeoutCount: Int = 0
  ) {
    try {
      val response = retrofitService.createMessage(loginToken, CreateMessageBody(roomId, messageBody)).awaitResponse()

      if(response.isSuccessful) {
        successCallback(response)
        return
      }
    } catch (error: Throwable) {
      if(error is SocketTimeoutException && timeoutCount < 2) {
        return createMessage(loginToken, messageBody, roomId, successCallback, failureCallback, timeoutCount + 1)
      }
    }

    failureCallback()
  }

  suspend fun registerFirebaseRegistrationToken(
    loginToken: String,
    successCallback: (response: Response<RegisterFirebaseRegistrationTokenResponse>) -> Unit,
    failureCallback: () -> Unit,
    timeoutCount: Int = 0
  ) {
    try {
      val response = retrofitService.registerFirebaseRegistrationToken(loginToken).awaitResponse()
      successCallback(response)
      return

    } catch (error: Throwable) {
      if(error is SocketTimeoutException && timeoutCount < 2) {
        return registerFirebaseRegistrationToken(loginToken, successCallback, failureCallback, timeoutCount + 1)
      }
    }

    failureCallback()
  }

  suspend fun logout(
    loginToken: String,
    successCallback: (response: Response<LogoutResponse>) -> Unit,
    failureCallback: () -> Unit,
    timeoutCount: Int = 0
  ) {
    try {
      val response = retrofitService.logout(loginToken).awaitResponse()
      successCallback(response)
      return
    } catch (error: Throwable) {
      if(error is SocketTimeoutException && timeoutCount < 2) {
        return logout(loginToken, successCallback, failureCallback, timeoutCount + 1)
      }
    }

    failureCallback()
  }

  private const val TAG = "GahoelChatApiService"
}

