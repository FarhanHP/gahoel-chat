package com.farhanhp.gahoelchat.services

import com.farhanhp.gahoelchat.classes.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

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

  fun login(
    email: String,
    password: String,
    firebaseRegisterToken: String,
    successCallback: (response: Response<LoginResponse>)->Unit,
    failureCallback: ()->Unit
  ) {
    retrofitService.login(LoginBody(email, password, firebaseRegisterToken)).enqueue(
      CallbackWrapper<LoginResponse>({successCallback(it)}, {failureCallback()})
    )
  }

  fun register(
    email: String,
    password: String,
    name: String,
    successCallback: (response: Response<RegisterResponse>)->Unit,
    failureCallback: ()->Unit
  ) {
    retrofitService.register(RegisterBody(email, password, name)).enqueue(
      CallbackWrapper<RegisterResponse>({successCallback(it)}, {failureCallback()})
    )
  }

  fun getProfile(
    loginToken: String,
    successCallback: (response: Response<GetProfileResponse>) -> Unit,
    failureCallback: () -> Unit
  ) {
    retrofitService.getProfile(loginToken).enqueue(
      CallbackWrapper<GetProfileResponse>({successCallback(it)}, {failureCallback()})
    )
  }

  fun getRooms(
    loginToken: String,
    successCallback: (response: Response<GetRoomsResponse>) -> Unit,
    failureCallback: () -> Unit
  ) {
    retrofitService.getRooms(loginToken).enqueue(
      CallbackWrapper<GetRoomsResponse>({successCallback(it)}, {failureCallback()})
    )
  }

  fun createNewMessage(
    loginToken: String,
    recipientEmail: String,
    messageBody: String,
    successCallback: (response: Response<CreateNewMessageResponse>) -> Unit,
    failureCallback: () -> Unit
  ) {
    retrofitService.createNewMessage(loginToken, CreateNewMessageBody(recipientEmail, messageBody)).enqueue(
      CallbackWrapper<CreateNewMessageResponse>({successCallback(it)}, {failureCallback()})
    )
  }

  fun createMessage(
    loginToken: String,
    messageBody: String,
    roomId: String,
    successCallback: (response: Response<CreateMessageResponse>) -> Unit,
    failureCallback: () -> Unit
  ) {
    retrofitService.createMessage(loginToken, CreateMessageBody(roomId, messageBody)).enqueue(
      CallbackWrapper<CreateMessageResponse>({successCallback(it)}, {failureCallback()})
    )
  }

  fun registerFirebaseRegistrationToken(
    loginToken: String,
    successCallback: (response: Response<RegisterFirebaseRegistrationTokenResponse>) -> Unit,
    failureCallback: () -> Unit
  ) {
    retrofitService.registerFirebaseRegistrationToken(loginToken).enqueue(
      CallbackWrapper<RegisterFirebaseRegistrationTokenResponse>({successCallback(it)}, {failureCallback()})
    )
  }

  fun logout(
    loginToken: String,
    successCallback: (response: Response<LogoutResponse>) -> Unit,
    failureCallback: () -> Unit
  ) {
    retrofitService.logout(loginToken).enqueue(
      CallbackWrapper<LogoutResponse>({successCallback(it)}, {failureCallback()})
    )
  }
}

