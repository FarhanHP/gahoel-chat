package com.farhanhp.gahoelchat.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.farhanhp.gahoelchat.R
import com.google.android.material.button.MaterialButton

class ChatRoomAppbar(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
  private val backButton: MaterialButton
  private val titleTextView: TextView
  private val profilePhoto: ImageView

  init {
    LayoutInflater.from(context).inflate(R.layout.component_chat_room_appbar, this, true)

    backButton = findViewById(R.id.backBtn)
    titleTextView = findViewById(R.id.title)
    profilePhoto = findViewById(R.id.profilePhoto)

    context.theme.obtainStyledAttributes(attrs, R.styleable.SecondaryAppbar, 0, 0).apply {
      val title = getString(R.styleable.SecondaryAppbar_title)
      if(title != null) {
        setTitle(title)
      }
    }
  }

  fun setBackButtonClickListener(fn: () -> Unit) {
    backButton.setOnClickListener{ fn() }
  }

  fun setTitle(title: String) {
    titleTextView.text = title
  }

  fun setProfilePhoto(imageUrl: String) {
    val imageUri = imageUrl.toUri().buildUpon().scheme("https").build()
    Glide.with(profilePhoto.context).load(imageUri).into(profilePhoto)
  }
}