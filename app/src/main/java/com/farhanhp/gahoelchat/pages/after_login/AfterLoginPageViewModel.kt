package com.farhanhp.gahoelchat.pages.after_login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhanhp.gahoelchat.services.GahoelChatApiService
import com.farhanhp.gahoelchat.classes.GetRoomsResponse
import com.farhanhp.gahoelchat.classes.Message
import com.farhanhp.gahoelchat.classes.Room
import com.farhanhp.gahoelchat.classes.User
import kotlinx.coroutines.launch
import retrofit2.Response

class AfterLoginPageViewModel(
  private val loginUser: User,
  private val loginToken: String,
  private val fetchRoomsSuccessCallback: () -> Unit,
  private val fetchRoomsFailCallback: () -> Unit
): ViewModel() {
  private val _fetchingRooms = MutableLiveData(false)
  val fetchingRooms: LiveData<Boolean> get() = _fetchingRooms

  var selectedRoom: Room? = null

  private val _rooms = MutableLiveData(listOf<Room>())
  val rooms: LiveData<List<Room>> get() = _rooms

  init {
    fetchRooms()
  }

  private fun fetchRooms() {
    fun success(response: Response<GetRoomsResponse>) {
      _fetchingRooms.value = false
      when(response.code()) {
        200 -> {
          val body = response.body()
          if(body != null) {
            _rooms.value = body.content.rooms
            fetchRoomsSuccessCallback()
          }
        }
        else -> {
          fetchRoomsFailCallback()
        }
      }
    }

    fun fail() {
      _fetchingRooms.value = false
      fetchRoomsFailCallback()
    }

    _fetchingRooms.value = true
    viewModelScope.launch {
      GahoelChatApiService.getRooms(loginToken, {success(it)}, {fail()})
    }
  }

  fun insertNewRoom(senderUserName: String, senderUserId: String, senderImage: String, roomId: String, lastInteractAt: Long, createdAt: Long, updatedAt: Long, firstMessageBody: String, firstMessageId: String) {
    insertNewRoom(
      Room(
        senderUserName,
        senderImage,
        roomId,
        lastInteractAt,
        createdAt,
        updatedAt,
        listOf(
          Message(
            firstMessageId,
            firstMessageBody,
            senderUserId == loginUser._id,
            createdAt
          )
        )
      )
    )
  }

  fun insertNewRoom(room: Room) {
    if(_rooms.value != null) {
      val newRooms = (_rooms.value as List).toMutableList()
      newRooms.add(0, room)
      val objectIds = mutableListOf<String>()

      // remove duplicate
      val uniqueNewRooms = mutableListOf<Room>()
      newRooms.forEach {
        val currentObjectId = it._id
        if(!objectIds.contains(currentObjectId)) {
          objectIds.add(it._id)
          uniqueNewRooms.add(it)
        }
      }

      _rooms.value = uniqueNewRooms
    }
  }

  fun insertNewMessage(roomId: String, senderUserId: String, messageId: String, messageBody: String, createdAt: Long) {
    insertNewMessage(roomId, Message(
      messageId,
      messageBody,
      senderUserId == loginUser._id,
      createdAt,
    ))
  }

  private fun insertNewMessage(roomId: String, newMessage: Message) {
    val newRooms = mutableListOf<Room>()
    var selectedRoom: Room? = null
    _rooms.value?.forEach{
      if(it._id != roomId) {
        newRooms.add(it)
      } else {
        val newMessages = it.messages.toMutableList()
        newMessages.add(0, newMessage)
        it.messages = newMessages
        selectedRoom = it
      }
    }

    if(selectedRoom != null) {
      newRooms.add(0, selectedRoom as Room)
      _rooms.value = newRooms
    }
  }

  fun getRoomById(roomId: String): Room? {
    val rooms = _rooms.value
    if(rooms != null) {
      for(room in rooms) {
        if(room._id == roomId) {
          return room
        }
      }
    }

    return null
  }
}