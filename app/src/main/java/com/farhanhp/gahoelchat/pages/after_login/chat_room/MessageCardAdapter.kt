package com.farhanhp.gahoelchat.pages.after_login.chat_room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.farhanhp.gahoelchat.DEFAULT_PP
import com.farhanhp.gahoelchat.R
import com.farhanhp.gahoelchat.api.Message
import com.farhanhp.gahoelchat.toDateString
import java.util.*

class MessageCardAdapter(
  private val roomImage: String
): RecyclerView.Adapter<MessageCardAdapter.ViewHolder>() {
  class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val firstPersonRoot: LinearLayout = itemView.findViewById(R.id.firstPerson)
    val firstPersonTime: TextView = itemView.findViewById(R.id.firstPersonTime)
    val firstPersonBody: TextView = itemView.findViewById(R.id.firstPersonBody)
    val secondPersonRoot: LinearLayout = itemView.findViewById(R.id.secondPerson)
    val secondPersonTime: TextView = itemView.findViewById(R.id.secondPersonTime)
    val secondPersonImage: ImageView = itemView.findViewById(R.id.secondPersonImage)
    val secondPersonBody: TextView = itemView.findViewById(R.id.secondPersonBody)
  }

  var data = listOf<Message>()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_message_card, parent, false)
    return ViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = data[position]
    val dateString = when(item.createdAt) {
      -1L -> "Sending..."
      else -> toDateString(Date(item.createdAt))
    }
    if(item.isOwner) {
      holder.let {
        it.firstPersonRoot.visibility = View.VISIBLE
        it.secondPersonRoot.visibility = View.GONE
        it.firstPersonBody.text = item.messageBody
        it.firstPersonTime.text = dateString
      }
    } else {
      holder.let {
        it.firstPersonRoot.visibility = View.GONE
        it.secondPersonRoot.visibility = View.VISIBLE
        it.secondPersonBody.text = item.messageBody
        it.secondPersonTime.text = dateString

        val imageView = it.secondPersonImage
        val imageUri = (when(roomImage.length){
          0 -> DEFAULT_PP
          else -> roomImage
        }).toUri().buildUpon().scheme("https").build()
        Glide.with(imageView.context).load(imageUri).into(imageView)
      }
    }
  }

  override fun getItemCount() = data.size
}