package com.farhanhp.gahoelchat.pages.login

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
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
import com.farhanhp.gahoelchat.api.LoginResponse
import com.farhanhp.gahoelchat.components.TextField
import com.farhanhp.gahoelchat.databinding.PageLoginBinding
import com.google.android.material.button.MaterialButton

class LoginPage : Fragment() {
  private lateinit var binding: PageLoginBinding
  private lateinit var emailInput: TextField
  private lateinit var passwordInput: TextField
  private lateinit var loginButton: MaterialButton
  private lateinit var registerButton: MaterialButton
  private lateinit var viewModel: LoginPageViewModel
  private lateinit var mainActivityViewModel: MainActivityViewModel
  private lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DataBindingUtil.inflate(inflater, R.layout.page_login, container, false)
    emailInput = binding.emailInput
    emailInput.setAttribute("Email", InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
    passwordInput = binding.passwordInput
    passwordInput.setAttribute(
      "Password",
      InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
    )
    loginButton = binding.loginButton
    loginButton.setOnClickListener{
      handleLogin()
    }
    registerButton = binding.registerButton
    registerButton.setOnClickListener {
      redirectToRegisterPage()
    }

    setViewModel()

    return binding.root
  }

  private fun setViewModel() {
    mainActivityViewModelFactory = MainActivityViewModelFactory(requireActivity() as MainActivity)
    mainActivityViewModel = ViewModelProvider(requireActivity(), mainActivityViewModelFactory).get(MainActivityViewModel::class.java)
    viewModel = ViewModelProvider(this).get(LoginPageViewModel::class.java)
    viewModel.emailError.observe(viewLifecycleOwner) {
      emailInput.setError(it)
    }
    viewModel.emailMessage.observe(viewLifecycleOwner) {
      emailInput.setMessage(it)
    }
    viewModel.passwordError.observe(viewLifecycleOwner) {
      passwordInput.setError(it)
    }
    viewModel.passwordMessage.observe(viewLifecycleOwner) {
      passwordInput.setMessage(it)
    }
    viewModel.loading.observe(viewLifecycleOwner) {
      loginButton.isClickable = !it
      loginButton.isEnabled = !it
      registerButton.isClickable = !it
      registerButton.isEnabled = !it
      loginButton.text = when(it) {
        true -> "Logging In..."
        else -> "Login"
      }
    }
  }

  private fun onLoginSuccess(loginResponse: LoginResponse) {
    mainActivityViewModel.loginToken = loginResponse.content.loginToken
    mainActivityViewModel.loginUser = loginResponse.content.user

    findNavController().navigate(LoginPageDirections.actionLoginPageToAfterLoginPage())
  }

  private fun redirectToRegisterPage() {
    findNavController().navigate(LoginPageDirections.actionLoginPageToRegisterPage())
  }

  private fun handleLogin() {
    viewModel.login(emailInput.text.toString(), passwordInput.text.toString(), mainActivityViewModel.firebaseRegisterToken as String, {
      onLoginSuccess(it)
    }){
      Toast.makeText(context, "Something wrong, Please try again", Toast.LENGTH_LONG).show()
    }
  }
}