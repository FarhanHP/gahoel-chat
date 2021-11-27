package com.farhanhp.gahoelchat.pages.after_login.home

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
import com.farhanhp.gahoelchat.classes.Room
import com.farhanhp.gahoelchat.toDateString
import java.util.*

class RoomAdapter: RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
  class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val root: LinearLayout = itemView.findViewById(R.id.root)
    val profilePhoto: ImageView = itemView.findViewById(R.id.profilePhoto)
    val nameTextView: TextView = itemView.findViewById(R.id.name)
    val lastMessageTextView: TextView = itemView.findViewById(R.id.lastMessage)
    val timeTextView: TextView = itemView.findViewById(R.id.time)
  }

  var data = listOf<Room>()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  private var onItemClickListener: ((item: Room) -> Unit)? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_room, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = data[position]

    if(onItemClickListener != null) {
      holder.root.setOnClickListener{
        (onItemClickListener as (item: Room) -> Unit)(item)
      }
    }

    val imageUri = (when(item.roomImage.length){
      0 -> DEFAULT_PP
      else -> item.roomImage
    }).toUri().buildUpon().scheme("https").build()
    val imageView = holder.profilePhoto
    Glide.with(imageView.context).load(imageUri).into(imageView)
    holder.nameTextView.text = item.roomName
    holder.lastMessageTextView.text = item.messages[0].messageBody
    holder.timeTextView.text = toDateString(Date(item.messages[0].createdAt))
  }

  override fun getItemCount() = data.size

  fun setOnItemClickListener(fn: (data: Room) -> Unit) {
    onItemClickListener = fn
  }
}