package com.farhanhp.gahoelchat.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.farhanhp.gahoelchat.R
import com.google.android.material.button.MaterialButton

class SecondaryAppbar(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
  private val backBtn: MaterialButton
  private val titleTextView: TextView

  init {
    LayoutInflater.from(context).inflate(R.layout.component_secondary_appbar, this, true)

    backBtn = findViewById(R.id.backBtn)
    titleTextView = findViewById(R.id.title)

    context.theme.obtainStyledAttributes(attrs, R.styleable.SecondaryAppbar, 0, 0).apply {
      val title = getString(R.styleable.SecondaryAppbar_title)
      if(title != null) {
        setTitle(title)
      }
    }
  }

  fun setTitle(title: String) {
    titleTextView.text = title
  }

  fun setBackBtnClickListener(fn: () -> Unit) {
    backBtn.setOnClickListener{ fn() }
  }
}