package com.farhanhp.gahoelchat.pages.register

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
import com.farhanhp.gahoelchat.R
import com.farhanhp.gahoelchat.components.TextField
import com.farhanhp.gahoelchat.databinding.PageRegisterBinding
import com.google.android.material.button.MaterialButton

class RegisterPage : Fragment() {
  private lateinit var binding: PageRegisterBinding
  private lateinit var emailInput: TextField
  private lateinit var nameInput: TextField
  private lateinit var passwordInput: TextField
  private lateinit var repeatPasswordInput: TextField
  private lateinit var registerButton: MaterialButton
  private lateinit var loginButton: MaterialButton
  private lateinit var viewModel: RegisterPageViewModel
  private lateinit var viewModelFactory: RegisterPageViewModelFactory

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.page_register, container, false)

    emailInput = binding.emailInput
    emailInput.setAttribute("Email*", InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
    nameInput = binding.nameInput
    nameInput.setAttribute("Name*", InputType.TYPE_CLASS_TEXT)
    passwordInput = binding.passwordInput
    passwordInput.setAttribute("Password*", InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD)
    repeatPasswordInput = binding.repeatPasswordInput
    repeatPasswordInput.setAttribute("Repeat Password*", InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD)

    registerButton = binding.registerButton
    registerButton.setOnClickListener{
      handleRegister()
    }

    loginButton = binding.loginButton
    loginButton.setOnClickListener{
      redirectToLogin()
    }

    setViewModel()

    return binding.root
  }

  private fun setViewModel() {
    viewModelFactory = RegisterPageViewModelFactory ({
      onRegisterSuccess()
    }) {
      Toast.makeText(context, "Something Wrong, Please try again", Toast.LENGTH_LONG).show()
    }
    viewModel = ViewModelProvider(this, viewModelFactory).get(RegisterPageViewModel::class.java)
    viewModel.emailError.observe(viewLifecycleOwner) {
      emailInput.setError(it)
    }
    viewModel.emailMessage.observe(viewLifecycleOwner) {
      emailInput.setMessage(it)
    }
    viewModel.nameError.observe(viewLifecycleOwner) {
      nameInput.setError(it)
    }
    viewModel.nameMessage.observe(viewLifecycleOwner) {
      nameInput.setMessage(it)
    }
    viewModel.passwordError.observe(viewLifecycleOwner) {
      passwordInput.setError(it)
    }
    viewModel.passwordMessage.observe(viewLifecycleOwner) {
      passwordInput.setMessage(it)
    }
    viewModel.repeatPasswordError.observe(viewLifecycleOwner) {
      repeatPasswordInput.setError(it)
    }
    viewModel.repeatPasswordMessage.observe(viewLifecycleOwner) {
      repeatPasswordInput.setMessage(it)
    }
    viewModel.loading.observe(viewLifecycleOwner) {
      loginButton.isClickable = !it
      loginButton.isEnabled = !it
      registerButton.isClickable = !it
      registerButton.isEnabled = !it
      registerButton.text = when(it) {
        true -> "Registering..."
        else -> "Register"
      }
    }
  }

  private fun onRegisterSuccess() {
    Toast.makeText(context, "Register Success", Toast.LENGTH_LONG).show()
    redirectToLogin()
  }

  private fun redirectToLogin() {
    findNavController().navigate(RegisterPageDirections.actionRegisterPageToLoginPage())
  }

  private fun handleRegister() {
    viewModel.register(
      emailInput.text.toString(),
      nameInput.text.toString(),
      passwordInput.text.toString(),
      repeatPasswordInput.text.toString()
    )
  }
}