package com.farhanhp.gahoelchat.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.farhanhp.gahoelchat.R
import com.google.android.material.button.MaterialButton

class PrimaryAppbar(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
  private val menuBtn: MaterialButton;

  init {
    LayoutInflater.from(context).inflate(R.layout.component_primary_appbar, this, true)
    menuBtn = findViewById(R.id.menuBtn)
  }

  fun setMenuBtnClickListener(fn: () -> Unit) {
    menuBtn.setOnClickListener{fn()}
  }
}