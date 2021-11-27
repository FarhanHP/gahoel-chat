package com.farhanhp.gahoelchat.classes

data class LoginBody(
  val email: String,
  val password: String,
  val firebaseRegisterToken: String,
)

data class RegisterBody(
  val email: String,
  val password: String,
  val name: String,
)

data class GetRoomsResponseBody(
  val rooms: List<Room>
)

data class CreateNewMessageBody(
  val recipientEmail: String,
  val messageBody: String,
)

data class CreateMessageBody(
  val roomId: String,
  val messageBody: String
)

data class CreateMessageResponse(
  val message: String,
  val content: CreateMessageResponseContent
)

data class GetRoomsResponse(
  val message: String,
  val content: GetRoomsResponseBody
);

data class LoginResponse(
  val message: String,
  val content: LoginResponseContent
)

data class GetProfileResponse(
  val message: String,
  val content: User,
)

data class RegisterFirebaseRegistrationTokenResponse(
  val message: String
)

data class LogoutResponse(
  val message: String
)

data class RegisterResponse(
  val message: String
)

data class CreateNewMessageResponse(
  val message: String,
  val content: Room,
)

data class LoginResponseContent(
  val loginToken: String,
  val user: User,
)

data class CreateMessageResponseContent(
  val message: Message
)

data class Room(
  val roomName: String,
  val roomImage: String,
  val _id: String,
  val lastInteractAt: Long,
  val createdAt: Long,
  val updatedAt: Long,
  var messages: List<Message>
)

data class Message(
  val _id: String,
  val messageBody: String,
  val isOwner: Boolean,
  val createdAt: Long
)

data class User(
  val _id: String,
  val email: String,
  val imageUrl: String,
  val name: String,
)