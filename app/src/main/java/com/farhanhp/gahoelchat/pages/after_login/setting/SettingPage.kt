package com.farhanhp.gahoelchat.pages.after_login.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.farhanhp.gahoelchat.*
import com.farhanhp.gahoelchat.classes.SecondaryPage
import com.farhanhp.gahoelchat.components.SecondaryAppbar
import com.farhanhp.gahoelchat.databinding.PageSettingBinding
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPage
import com.farhanhp.gahoelchat.pages.after_login.AfterLoginPageDirections
import com.google.android.material.button.MaterialButton

class SettingPage : SecondaryPage() {
  private lateinit var binding: PageSettingBinding
  private lateinit var appbar: SecondaryAppbar
  private lateinit var logoutButton: MaterialButton
  private lateinit var loadingDialog: RelativeLayout
  private lateinit var profilePhoto: ImageView
  private lateinit var nameTextView: TextView
  private lateinit var emailTextView: TextView
  private lateinit var mainActivityViewModel: MainActivityViewModel
  private lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DataBindingUtil.inflate(inflater, R.layout.page_setting, container, false)
    profilePhoto = binding.profilePhoto
    nameTextView = binding.name
    emailTextView = binding.email
    appbar = binding.appbar
    appbar.setBackBtnClickListener {
      openPriorPage()
    }
    logoutButton = binding.logoutButton
    logoutButton.setOnClickListener{
      logout()
    }
    loadingDialog = binding.loadingDialog
    setMainActivityViewModel()
    return binding.root
  }

  private fun setMainActivityViewModel() {
    mainActivityViewModelFactory = MainActivityViewModelFactory(requireActivity() as MainActivity)
    mainActivityViewModel = ViewModelProvider(requireActivity(), mainActivityViewModelFactory).get(MainActivityViewModel::class.java)
    mainActivityViewModel.loggingOut.observe(viewLifecycleOwner) {
      if(it) {
        loadingDialog.visibility = View.VISIBLE
      } else {
        loadingDialog.visibility = View.GONE
      }
    }

    mainActivityViewModel.loginUser.let {
      if(it != null) {
        nameTextView.text = it.name
        emailTextView.text = it.email

        val imageUrl = it.imageUrl
        var imageUri = (when(imageUrl.length) {
          0 -> DEFAULT_PP
          else -> imageUrl
        }).toUri().buildUpon().scheme("https").build()
        Glide.with(profilePhoto.context).load(imageUri).into(profilePhoto)
      }
    }
  }

  private fun logout() {
    mainActivityViewModel.logout({redirectToLoginPage()}, {})
  }

  private fun redirectToLoginPage() {
    val grandParentFragment = parentFragment?.parentFragment
    if(grandParentFragment is AfterLoginPage) {
      grandParentFragment.findNavController().navigate(AfterLoginPageDirections.actionAfterLoginPageToLoginPage())
    }
  }
}