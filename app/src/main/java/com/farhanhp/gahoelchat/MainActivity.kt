package com.farhanhp.gahoelchat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
  private lateinit var broadcastReceiver: BroadcastReceiver
  private lateinit var mainActivityViewModel: MainActivityViewModel
  private lateinit var mainActivityViewModelFactory: MainActivityViewModelFactory

  override fun onResume() {
    super.onResume()
    val intentFilter = IntentFilter("receive_message")
    broadcastReceiver = object: BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
          when(it.getStringExtra("type") as String) {
            "message" -> {
              mainActivityViewModel.passNewMessageFromFCM(
                it.getStringExtra("roomId") as String,
                it.getStringExtra("senderUserId") as String,
                it.getStringExtra("_id") as String,
                it.getStringExtra("messageBody") as String,
                (it.getStringExtra("createdAt") as String).toLong(),
              )
            } else -> {
              mainActivityViewModel.passNewRoomFromFCM(
                it.getStringExtra("senderUserName") as String,
                it.getStringExtra("senderUserId") as String,
                it.getStringExtra("senderImage") as String,
                it.getStringExtra("_id") as String,
                (it.getStringExtra("lastInteractAt") as String).toLong(),
                (it.getStringExtra("createdAt") as String).toLong(),
                (it.getStringExtra("updatedAt") as String).toLong(),
                it.getStringExtra("firstMessageBody") as String,
                it.getStringExtra("firstMessageId") as String
              )
            }
          }
        }
      }
    }

    registerReceiver(broadcastReceiver, intentFilter)
  }

  override fun onPause() {
    super.onPause()

    unregisterReceiver(broadcastReceiver)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    setModel()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  private fun setModel() {
    mainActivityViewModelFactory = MainActivityViewModelFactory(this)
    mainActivityViewModel = ViewModelProvider(this, mainActivityViewModelFactory).get(MainActivityViewModel::class.java)
  }
}