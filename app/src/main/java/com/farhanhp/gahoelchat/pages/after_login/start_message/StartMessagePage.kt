package com.farhanhp.gahoelchat.pages.after_login.start_message

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.farhanhp.gahoelchat.MainActivity
import com.farhanhp.gahoelchat.MainActivityViewModel
import com.farhanhp.gahoelchat.MainActivityViewModelFactory
import com.farhanhp.gahoelchat.R
import com.farhanhp.gahoelchat.classes.Room
import com.farhanhp.gahoelchat.classes.SecondaryPage
import com.farhanhp.gahoelchat.classes.User
import com.farhanhp.gahoelchat.components.SecondaryAppbar
import com.farhanhp.gahoelchat.components.TextField
import com.farhanhp.gahoelchat.databinding.PageStartMessageBinding
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPage
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPageViewModel
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPageViewModelFactory
import com.google.android.material.button.MaterialButton

class StartMessagePage : SecondaryPage() {
  private lateinit var binding: PageStartMessageBinding
  private lateinit var appbar: SecondaryAppbar
  private lateinit var emailInput: TextField
  private lateinit var bodyInput: TextField
  private lateinit var sendMessageButton: MaterialButton
  private lateinit var afterLoginPageParent: AfterLoginPage
  private lateinit var mainActivityViewModel: MainActivityViewModel
  private lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
  private lateinit var afterLoginPageViewModel: AfterLoginPageViewModel
  private lateinit var afterLoginPageViewModelFactory: AfterLoginPageViewModelFactory
  private lateinit var startMessagePageViewModel: StartMessagePageViewModel
  private lateinit var startMessagePageViewModelFactory: StartMessagePageViewModelFactory

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DataBindingUtil.inflate(inflater, R.layout.page_start_message, container, false)
    setModels()
    setComponents()
    setEvents()
    return binding.root
  }

  private fun setModels() {
    afterLoginPageParent = parentFragment?.parentFragment as AfterLoginPage
    mainActivityViewModelFactory = MainActivityViewModelFactory(requireActivity() as MainActivity)
    mainActivityViewModel = ViewModelProvider(requireActivity(), mainActivityViewModelFactory).get(MainActivityViewModel::class.java)

    val loginToken = mainActivityViewModel.loginToken as String
    val loginUser = mainActivityViewModel.loginUser as User

    afterLoginPageViewModelFactory = AfterLoginPageViewModelFactory(loginUser, loginToken, {}, {})
    afterLoginPageViewModel = ViewModelProvider(afterLoginPageParent, afterLoginPageViewModelFactory).get(AfterLoginPageViewModel::class.java)
    startMessagePageViewModelFactory = StartMessagePageViewModelFactory(mainActivityViewModel.loginToken as String)
    startMessagePageViewModel = ViewModelProvider(this, startMessagePageViewModelFactory).get(StartMessagePageViewModel::class.java)
  }

  private fun setComponents() {
    appbar = binding.appbar
    appbar.setBackBtnClickListener { openPriorPage() }
    emailInput = binding.emailInput
    emailInput.setAttribute("Recipient Email", InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
    bodyInput = binding.bodyInput
    bodyInput.setAttribute("Write your message here...", InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE + InputType.TYPE_TEXT_FLAG_MULTI_LINE)
    sendMessageButton = binding.sendMessageButton
  }

  private fun setEvents() {
    startMessagePageViewModel.messageBodyError.observe(viewLifecycleOwner) {
      bodyInput.setError(it)
    }
    startMessagePageViewModel.messageBodyMessage.observe(viewLifecycleOwner) {
      bodyInput.setMessage(it)
    }
    startMessagePageViewModel.emailError.observe(viewLifecycleOwner) {
      emailInput.setError(it)
    }
    startMessagePageViewModel.emailMessage.observe(viewLifecycleOwner) {
      emailInput.setMessage(it)
    }
    startMessagePageViewModel.loading.observe(viewLifecycleOwner) {
      if(it) {
        setSendMessageButtonLoading()
      } else {
        setSendMessageButtonDefault()
      }
    }
    sendMessageButton.setOnClickListener{
      createMessage()
    }
  }

  private fun setSendMessageButtonLoading() {
    sendMessageButton.text = "Sending Message..."
    sendMessageButton.isEnabled = false
    sendMessageButton.isClickable = false
  }

  private fun setSendMessageButtonDefault() {
    sendMessageButton.text = "Send Message"
    sendMessageButton.isEnabled = true
    sendMessageButton.isClickable = true
  }

  private fun createMessage() {
    val recipientEmail = emailInput.text.toString()
    val messageBody = bodyInput.text.toString()

    startMessagePageViewModel.createNewMessage(
      recipientEmail,
      messageBody,
      {
        afterLoginPageViewModel.insertNewRoom(it)
        redirectToChatRoomPage(it)
      },
      {
        Toast.makeText(context, "Fail to create a message, try again later.", Toast.LENGTH_LONG).show()
      }
    )
  }

  private fun redirectToChatRoomPage(room: Room) {
    afterLoginPageViewModel.selectedRoom = room
    findNavController().navigate(StartMessagePageDirections.actionStartMessagePageToChatRoomPage())
  }
}