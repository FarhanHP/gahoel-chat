package com.farhanhp.gahoelchat.pages.after_login.chat_room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farhanhp.gahoelchat.*
import com.farhanhp.gahoelchat.classes.Message
import com.farhanhp.gahoelchat.classes.Room
import com.farhanhp.gahoelchat.classes.SecondaryPage
import com.farhanhp.gahoelchat.classes.User
import com.farhanhp.gahoelchat.components.ChatRoomAppbar
import com.farhanhp.gahoelchat.databinding.PageChatRoomBinding
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPage
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPageViewModel
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPageViewModelFactory
import com.google.android.material.button.MaterialButton

class ChatRoomPage : SecondaryPage() {
  private lateinit var mainActivityViewModel: MainActivityViewModel
  private lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
  private lateinit var afterLoginPageViewModel: AfterLoginPageViewModel
  private lateinit var afterLoginPageViewModelFactory: AfterLoginPageViewModelFactory
  private lateinit var chatRoomPageViewModel: ChatRoomPageViewModel
  private lateinit var chatRoomPageViewModelFactory: ChatRoomPageViewModelFactory
  private lateinit var messageList: RecyclerView
  private lateinit var adapter: MessageCardAdapter
  private lateinit var binding: PageChatRoomBinding
  private lateinit var appbar: ChatRoomAppbar
  private lateinit var afterLoginPageParent: AfterLoginPage
  private lateinit var messageInput: EditText
  private lateinit var sendMessageButton: MaterialButton

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DataBindingUtil.inflate(inflater, R.layout.page_chat_room, container, false)
    afterLoginPageParent = parentFragment?.parentFragment as AfterLoginPage
    setModels()

    mainActivityViewModel.addOnPassNewMessageFromFCMListener { roomId, senderUserId, messageId, messageBody, createdAt ->
      if(afterLoginPageViewModel.selectedRoom?._id == roomId) {
        chatRoomPageViewModel.insertMessageByFCM(senderUserId, messageId, messageBody, createdAt)
      }
    }

    setComponents()
    setStates()

    return binding.root
  }

  private fun setModels() {
    mainActivityViewModelFactory = MainActivityViewModelFactory(requireActivity() as MainActivity)
    mainActivityViewModel = ViewModelProvider(requireActivity(), mainActivityViewModelFactory).get(MainActivityViewModel::class.java)

    val loginToken = mainActivityViewModel.loginToken as String
    val loginUser = mainActivityViewModel.loginUser as User

    afterLoginPageViewModelFactory = AfterLoginPageViewModelFactory(loginUser, loginToken, {}, {})
    afterLoginPageViewModel = ViewModelProvider(afterLoginPageParent, afterLoginPageViewModelFactory).get(AfterLoginPageViewModel::class.java)

    chatRoomPageViewModelFactory = ChatRoomPageViewModelFactory(loginUser, loginToken, afterLoginPageViewModel.selectedRoom as Room)
    chatRoomPageViewModel = ViewModelProvider(this, chatRoomPageViewModelFactory).get(ChatRoomPageViewModel::class.java)
  }

  private fun setComponents() {
    messageInput = binding.messageInput
    sendMessageButton = binding.sendMessageButton

    sendMessageButton.setOnClickListener{
      sendMessage()
    }

    appbar = binding.appbar
    appbar.setBackButtonClickListener { openPriorPage() }
    messageList = binding.messageList
    adapter = MessageCardAdapter(afterLoginPageViewModel.selectedRoom?.roomImage as String)
    messageList.adapter = adapter
    val selectedRoom = afterLoginPageViewModel.selectedRoom
    selectedRoom?.let {
      appbar.setTitle(it.roomName)
      appbar.setProfilePhoto(when(it.roomImage.length){
        0 -> DEFAULT_PP
        else -> it.roomImage
      })
    }
  }

  private fun setStates() {
    chatRoomPageViewModel.messages.observe(viewLifecycleOwner) {
      adapter.data = it
    }
  }

  private fun sendMessage() {
    val messageBody = messageInput.text.toString()
    if(messageBody.isNotBlank()) {
      messageInput.setText("")
      chatRoomPageViewModel.createMessage(messageBody) {
        Toast.makeText(context, "Fail to send message, try again later", Toast.LENGTH_SHORT).show()
      }
    }
  }
}