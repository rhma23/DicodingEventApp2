package com.dicoding.dicodingeventapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingeventapp.database.FavoriteEvent
import com.dicoding.dicodingeventapp.FavoriteEventRepository
import kotlinx.coroutines.launch

class FavoriteEventViewModel(private val repository: FavoriteEventRepository) : ViewModel() {

    val allFavorites: LiveData<List<FavoriteEvent>> = repository.getAllFavorites()

    fun addFavorite(event: FavoriteEvent) {
        viewModelScope.launch {
            try {
                repository.insertFavorite(event)
                Log.d("FavoriteEventViewModel", "Event added: ${event.eventName}")
            } catch (e: Exception) {
                Log.e("FavoriteEventViewModel", "Failed to add favorite: ${e.message}")
            }
        }
    }

    fun removeFavorite(event: FavoriteEvent) {
        viewModelScope.launch {
            try {
                repository.deleteFavoriteByEventId(event.eventId)
                Log.d("FavoriteEventViewModel", "Event removed with eventId: ${event.eventId}")
            } catch (e: Exception) {
                Log.e("FavoriteEventViewModel", "Failed to remove favorite: ${e.message}")
            }
        }
    }
}
