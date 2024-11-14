package com.dicoding.dicodingeventapp

data class EventResponse(
    val error: Boolean,
    val message: String,
    val listEvents: List<Event> = emptyList(),
    val active: Int
)
