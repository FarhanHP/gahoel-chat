package com.farhanhp.gahoelchat.pages.after_login.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.farhanhp.gahoelchat.*
import com.farhanhp.gahoelchat.classes.Room
import com.farhanhp.gahoelchat.classes.User
import com.farhanhp.gahoelchat.components.PrimaryAppbar
import com.farhanhp.gahoelchat.databinding.PageHomeBinding
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPage
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPageViewModel
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPageViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomePage : Fragment() {
  private lateinit var binding: PageHomeBinding
  private lateinit var appbar: PrimaryAppbar
  private lateinit var fabButton: FloatingActionButton
  private lateinit var chatsDescription: TextView
  private lateinit var roomList: RecyclerView
  private lateinit var adapter: RoomAdapter
  private lateinit var afterLoginPageViewModel: AfterLoginPageViewModel
  private lateinit var afterLoginPageViewModelFactory: AfterLoginPageViewModelFactory
  private lateinit var mainActivityViewModel: MainActivityViewModel
  private lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
  private lateinit var afterLoginPageParent: AfterLoginPage

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DataBindingUtil.inflate(inflater, R.layout.page_home, container, false)
    afterLoginPageParent = parentFragment?.parentFragment as AfterLoginPage
    setViewModels()
    appbar = binding.appbar
    appbar.setMenuBtnClickListener {
      redirectToSettingPage()
    }
    fabButton = binding.fab
    fabButton.setOnClickListener{
      redirectToStartMessagePage()
    }
    chatsDescription = binding.chatsDescription
    roomList = binding.roomList
    adapter = RoomAdapter()
    adapter.setOnItemClickListener {
      redirectToChatRoomPage(it)
    }
    roomList.adapter = adapter

    afterLoginPageViewModel.fetchingRooms.observe(viewLifecycleOwner) {
      if(it) {
        chatsDescription.visibility = View.VISIBLE
        roomList.visibility = View.GONE
        chatsDescription.text = "Load your chats..."
      } else {
        chatsDescription.visibility = View.VISIBLE
        roomList.visibility = View.GONE
      }
    }

    afterLoginPageViewModel.rooms.observe(viewLifecycleOwner) {
      if(!(afterLoginPageViewModel.fetchingRooms.value as Boolean)) {
        if(it.isEmpty()) {
          chatsDescription.visibility = View.VISIBLE
          roomList.visibility = View.GONE
          chatsDescription.text = "No Chats :("
        } else {
          notificationClickHandler()
          adapter.data = it
          chatsDescription.visibility = View.GONE
          roomList.visibility = View.VISIBLE
          notificationClickHandler()
        }
      }
    }

    return binding.root
  }

  private fun setViewModels() {
    mainActivityViewModelFactory = MainActivityViewModelFactory(requireActivity() as MainActivity)
    mainActivityViewModel = ViewModelProvider(requireActivity(), mainActivityViewModelFactory).get(MainActivityViewModel::class.java)

    val loginToken = mainActivityViewModel.loginToken as String
    val loginUser = mainActivityViewModel.loginUser as User

    afterLoginPageViewModelFactory = AfterLoginPageViewModelFactory(loginUser, loginToken, {}, {})
    afterLoginPageViewModel = ViewModelProvider(afterLoginPageParent, afterLoginPageViewModelFactory).get(AfterLoginPageViewModel::class.java)
  }

  private fun redirectToSettingPage() {
    findNavController().navigate(HomePageDirections.actionHomePageToSettingPage())
  }

  private fun redirectToStartMessagePage() {
    findNavController().navigate(HomePageDirections.actionHomePageToStartMessagePage())
  }

  private fun redirectToChatRoomPage(room: Room) {
    afterLoginPageViewModel.selectedRoom = room
    findNavController().navigate(HomePageDirections.actionHomePageToChatRoomPage())
  }

  private fun notificationClickHandler() {
    val intent = activity?.intent

    if(intent != null) {
      val type = intent.getStringExtra("type")

      if(type != null) {
        val roomId = intent.getStringExtra(when(type){
          "message" -> "roomId"
          else ->  "_id"
        }) as String

        val room = afterLoginPageViewModel.getRoomById(roomId)
        if(room != null) {
          redirectToChatRoomPage(room)
          intent.removeExtra("type")
        }
      }
    }
  }
}