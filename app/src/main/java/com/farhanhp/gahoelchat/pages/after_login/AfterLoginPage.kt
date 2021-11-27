package com.farhanhp.gahoelchat.pages.after_login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.farhanhp.gahoelchat.MainActivity
import com.farhanhp.gahoelchat.MainActivityViewModel
import com.farhanhp.gahoelchat.MainActivityViewModelFactory
import com.farhanhp.gahoelchat.R
import com.farhanhp.gahoelchat.classes.User

class AfterLoginPage : Fragment() {
  private lateinit var mainActivityViewModel: MainActivityViewModel
  private lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
  private lateinit var afterLoginPageViewModel: AfterLoginPageViewModel
  private lateinit var afterLoginPageViewModelFactory: AfterLoginPageViewModelFactory

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    setModels()
    mainActivityViewModel.addOnPassNewMessageFromFCMListener {roomId, senderUserId, messageId, messageBody, createdAt ->
      afterLoginPageViewModel.insertNewMessage(roomId, senderUserId, messageId, messageBody, createdAt)
    }
    mainActivityViewModel.addOnPassNewRoomFromFCMListener { senderUserName, senderUserId, senderImage, roomId, lastInteractAt, createdAt, updatedAt, firstMessageBody, firstMessageId ->
      afterLoginPageViewModel.insertNewRoom(senderUserName, senderUserId, senderImage, roomId, lastInteractAt, createdAt, updatedAt, firstMessageBody, firstMessageId)
    }
    return inflater.inflate(R.layout.page_after_login, container, false)
  }

  private fun setModels() {
    mainActivityViewModelFactory = MainActivityViewModelFactory(requireActivity() as MainActivity)
    mainActivityViewModel = ViewModelProvider(requireActivity(), mainActivityViewModelFactory).get(MainActivityViewModel::class.java)

    val loginToken = mainActivityViewModel.loginToken as String
    val loginUser = mainActivityViewModel.loginUser as User

    afterLoginPageViewModelFactory = AfterLoginPageViewModelFactory(loginUser, loginToken, {}, {fetchRoomsFailCallback()})
    afterLoginPageViewModel = ViewModelProvider(this, afterLoginPageViewModelFactory).get(AfterLoginPageViewModel::class.java)
  }

  private fun fetchRoomsFailCallback() {
    Toast.makeText(context, "Fail to fetch chats, try again later :(", Toast.LENGTH_LONG).show()
  }
}