package com.dicoding.dicodingeventapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.dicodingeventapp.Event
import com.dicoding.dicodingeventapp.R

class EventAdapter(private var events: List<Event>, private val onItemClick: (Event) -> Unit) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName: TextView = itemView.findViewById(R.id.eventName)
        private val imageLogo: ImageView = itemView.findViewById(R.id.imageLogo)

        fun bind(event: Event, onItemClick: (Event) -> Unit) {
            eventName.text = event.name ?: "Unnamed Event"
            Glide.with(itemView.context)
                .load(event.imageLogo)
                .into(imageLogo)
            itemView.setOnClickListener { onItemClick(event) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position], onItemClick)
    }

    override fun getItemCount(): Int = events.size

    // fungsi untuk memperbarui data adapter
    fun updateData(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
}

