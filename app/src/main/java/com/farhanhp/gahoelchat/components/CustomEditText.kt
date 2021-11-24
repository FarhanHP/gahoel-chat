package com.farhanhp.gahoelchat.components

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.farhanhp.gahoelchat.R

class CustomEditText(context: Context, attrs: AttributeSet): androidx.appcompat.widget.AppCompatEditText(context, attrs) {
    fun setError(isError: Boolean) {
        backgroundTintList = ContextCompat.getColorStateList(context, when(isError) {
            true -> R.color.red_background_tint
            else -> R.color.white_background_tint
        })

        setHintTextColor(ContextCompat.getColor(context, when(isError) {
            true -> R.color.light_red
            else -> R.color.lightGrey
        }))
    }
}