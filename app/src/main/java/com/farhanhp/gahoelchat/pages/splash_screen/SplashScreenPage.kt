package com.farhanhp.gahoelchat.pages.splash_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.farhanhp.gahoelchat.MainActivity
import com.farhanhp.gahoelchat.MainActivityViewModel
import com.farhanhp.gahoelchat.MainActivityViewModelFactory
import com.farhanhp.gahoelchat.R
import com.farhanhp.gahoelchat.databinding.PageSplashScreenBinding

class SplashScreenPage : Fragment() {
  private lateinit var binding: PageSplashScreenBinding
  private lateinit var mainActivityModel: MainActivityViewModel
  private lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory
  private lateinit var loadingTextView: TextView
  private lateinit var viewModel: SplashScreenPageViewModel
  private lateinit var viewModelFactory: SplashScreenPageViewModelFactory

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DataBindingUtil.inflate(layoutInflater, R.layout.page_splash_screen, container, false)
    loadingTextView = binding.loadingText
    mainActivityViewModelFactory = MainActivityViewModelFactory(requireActivity() as MainActivity)
    mainActivityModel = ViewModelProvider(requireActivity(), mainActivityViewModelFactory).get(MainActivityViewModel::class.java)
    viewModelFactory = SplashScreenPageViewModelFactory({redirectToLoginPage()}, {redirectToHomePage()}, mainActivityModel.loginToken)
    viewModel = ViewModelProvider(this, viewModelFactory).get(SplashScreenPageViewModel::class.java)

    viewModel.loadingText.observe(viewLifecycleOwner) {
      loadingTextView.text = it
    }
    viewModel.firebaseRegisterToken.observe(viewLifecycleOwner) {
      mainActivityModel.firebaseRegisterToken = it
    }
    viewModel.loginToken.observe(viewLifecycleOwner) {
      mainActivityModel.loginToken = it
    }
    viewModel.loginUser.observe(viewLifecycleOwner) {
      mainActivityModel.loginUser = it
    }

    return binding.root
  }

  private fun redirectToLoginPage() {
    findNavController().navigate(SplashScreenPageDirections.actionSplashScreenPageToLoginPage())
  }

  private fun redirectToHomePage() {
    findNavController().navigate(SplashScreenPageDirections.actionSplashScreenPageToAfterLoginPage())
  }
}