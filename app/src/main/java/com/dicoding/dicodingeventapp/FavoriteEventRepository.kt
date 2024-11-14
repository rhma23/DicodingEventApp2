package com.dicoding.dicodingeventapp

import androidx.lifecycle.LiveData
import com.dicoding.dicodingeventapp.database.FavoriteEvent
import com.dicoding.dicodingeventapp.database.FavoriteEventDao

class FavoriteEventRepository(private val favoriteEventDao: FavoriteEventDao) {

    fun getAllFavorites(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavorites()
    }

    suspend fun insertFavorite(event: FavoriteEvent) {
        try {
            favoriteEventDao.insert(event)
        } catch (e: Exception) {

        }
    }

    suspend fun deleteFavoriteByEventId(eventId: String) {
        try {
            favoriteEventDao.deleteByEventId(eventId)
        } catch (e: Exception) {

        }
    }
}
