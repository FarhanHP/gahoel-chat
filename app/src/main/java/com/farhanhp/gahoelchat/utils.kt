package com.farhanhp.gahoelchat

import android.text.TextUtils
import java.util.*

fun isValidEmail(email: String): Boolean {
  return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
}

fun toDateString(date: Date): String {
  fun toNumberString(number: Int): String {
    if(number < 10) {
      return "0$number"
    }

    return number.toString()
  }
  return "${date.year + 1900}/${toNumberString(date.month)}/${toNumberString(date.date)} ${toNumberString(date.hours)}:${toNumberString(date.minutes)}"
}