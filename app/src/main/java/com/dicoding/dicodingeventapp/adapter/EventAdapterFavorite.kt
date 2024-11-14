package com.dicoding.dicodingeventapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.dicodingeventapp.DetailEventActivity
import com.dicoding.dicodingeventapp.R
import com.dicoding.dicodingeventapp.database.FavoriteEvent

class EventAdapterFavorite : ListAdapter<FavoriteEvent, EventAdapterFavorite.FavoriteViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteEvent = getItem(position)
        holder.bind(favoriteEvent)
    }

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName: TextView = itemView.findViewById(R.id.eventName)
        private val imageLogo: ImageView = itemView.findViewById(R.id.imageLogo)

        fun bind(event: FavoriteEvent) {
            eventName.text = event.eventName
            Glide.with(itemView.context).load(event.imageLogo).into(imageLogo)

            // tambahkan OnClickListener untuk navigasi ke halaman detail event
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailEventActivity::class.java)
                intent.putExtra("id", event.eventId.toInt()) // Mengirimkan ID event ke DetailEventActivity
                itemView.context.startActivity(intent)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FavoriteEvent>() {
        override fun areItemsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent): Boolean {
            return oldItem == newItem
        }
    }
}
