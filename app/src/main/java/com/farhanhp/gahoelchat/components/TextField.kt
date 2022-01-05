package com.farhanhp.gahoelchat.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.farhanhp.gahoelchat.R

class TextField(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
    private var messageTextView: TextView
    private var customEditText: CustomEditText
    private val redColor = ContextCompat.getColor(context, R.color.red)
    private val whiteColor = ContextCompat.getColor(context, R.color.white)

    val text get() = customEditText.text

    init {
        LayoutInflater.from(context).inflate(R.layout.component_textfield, this, true)

        orientation = VERTICAL
        messageTextView = findViewById(R.id.message)
        customEditText = findViewById(R.id.customEditText)
    }

    fun setAttribute(hint: String, inputType: Int) {
        customEditText.hint = hint
        customEditText.inputType = inputType

        // set fontfamily because it changed when inputType is changed programmatically
        customEditText.typeface = ResourcesCompat.getFont(context, R.font.poppins_semibold)
    }

    fun setError(isError: Boolean) {
        customEditText.setError(isError)
        messageTextView.setTextColor(when(isError){
            true -> redColor
            else -> whiteColor
        })
    }

    fun setMessage(message: String) {
        messageTextView.text = message
    }
}