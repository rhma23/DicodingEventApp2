package com.dicoding.dicodingeventapp.database



@Dao
interface FavoriteEventDao {
    @Query("DELETE FROM favorite_events WHERE eventId = :eventId")
    suspend fun deleteByEventId(eventId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM favorite_events")
    fun getAllFavorites(): LiveData<List<FavoriteEvent>>
}
