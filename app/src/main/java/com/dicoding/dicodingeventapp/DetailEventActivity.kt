package com.dicoding.dicodingeventapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.dicodingeventapp.database.AppDatabase
import com.dicoding.dicodingeventapp.database.FavoriteEvent
import com.dicoding.dicodingeventapp.viewModel.FavoriteEventViewModel
import com.dicoding.dicodingeventapp.viewModel.FavoriteEventViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.dicoding.dicodingeventapp.viewModel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DetailEventActivity : AppCompatActivity() {
    private var isFavorite = false
    private val viewModel: EventViewModel by viewModels()
    private lateinit var progressBar: ProgressBar
    private lateinit var favoriteEventViewModel: FavoriteEventViewModel
    private lateinit var event: FavoriteEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_event)

        val database = AppDatabase.getDatabase(this) // mendapatkan instance AppDatabase
        val favoriteEventDao = database.favoriteEventDao() // mendapatkan instance DAO
        val repository = FavoriteEventRepository(favoriteEventDao) // membuat repository dengan DAO

        // inisialisasi ViewModelFactory dan ViewModel
        val factory = FavoriteEventViewModelFactory(repository)
        favoriteEventViewModel = ViewModelProvider(this, factory)[FavoriteEventViewModel::class.java]

        progressBar = findViewById(R.id.progressBar2)

        val id = intent.getIntExtra("id", -1)

        if (id != -1) {
            Log.d(TAG, "EventId: $id")
            viewModel.fetchEventDetail(id.toString())
        } else {
            Log.e(TAG, "Invalid event ID")
            progressBar.visibility = View.INVISIBLE
        }

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.eventDetailLiveData.observe(this) { detailResponse ->
                progressBar.visibility = View.INVISIBLE
                detailResponse?.let {
                    val eventMediaCover = findViewById<ImageView>(R.id.mediaCover)
                    val cityName = findViewById<TextView>(R.id.cityName)
                    val eventName = findViewById<TextView>(R.id.eventName)
                    val ownerName = findViewById<TextView>(R.id.ownerName)
                    val summary = findViewById<TextView>(R.id.summary)
                    val sisaQuota = findViewById<TextView>(R.id.sisaQuota)
                    val beginTime = findViewById<TextView>(R.id.beginTime)
                    val durationValue = findViewById<TextView>(R.id.durationValue)
                    val eventDescription = findViewById<TextView>(R.id.eventDescription)
                    val registerButton = findViewById<Button>(R.id.registerButton)
                    val favoriteButton: FloatingActionButton = findViewById(R.id.favoriteButton)

                    // set data ke UI
                    eventName.text = detailResponse.event?.name ?: "Event name not available"
                    cityName.text = detailResponse.event?.cityName ?: ""
                    ownerName.text = detailResponse.event?.ownerName ?: ""
                    summary.text = detailResponse.event?.summary ?: ""

                    val beginTimeValue = detailResponse.event?.beginTime ?: ""
                    val onlyTime = extractTime(beginTimeValue)
                    beginTime.text = onlyTime

                    val registan = detailResponse.event?.registrants ?: 0
                    val quota = detailResponse.event?.quota ?: 0
                    val sisaKuota = (quota - registan)
                    sisaQuota.text = sisaKuota.toString()

                    val beginTimeInSeconds = convertToSeconds(detailResponse.event?.beginTime ?: "")
                    val endTimeInSeconds = convertToSeconds(detailResponse.event?.endTime ?: "")
                    val duration = (endTimeInSeconds - beginTimeInSeconds) / 3600
                    durationValue.text = duration.toString()

                    val htmlText = detailResponse.event?.description
                    if (htmlText != null) {
                        eventDescription.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        eventDescription.text = getString(R.string.description_not_available)
                    }

                    Glide.with(this)
                        .load(detailResponse.event?.mediaCover)
                        .into(eventMediaCover)

                    // memeriksa status favorit sebelum mengatur UI
                    checkIfFavorite(detailResponse.event?.id.toString(), favoriteButton)

                    // ketika tombol register diklik
                    registerButton.setOnClickListener {
                        val url = detailResponse.event?.link ?: "Event link not available"

                        // membuat intent untuk membuka URL di browser
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)

                        startActivity(intent)
                    }

                    // tombol favorit
                    favoriteButton.setOnClickListener {
                        Log.i(TAG, "onFavoriteButtonClicked: berhasil diklik")
                        isFavorite = !isFavorite
                        if (isFavorite) {
                            favoriteButton.setImageResource(R.drawable.ic_favorite) // Mengubah ke ikon 'favorite'

                            // logika untuk menambahkan event ke database favorit
                            val favoriteEvent = FavoriteEvent(
                                eventId = detailResponse.event?.id.toString(),
                                eventName = detailResponse.event?.name ?: "Unknown Event",
                                eventDescription = detailResponse.event?.description ?: "No Description",
                                imageLogo = detailResponse.event?.mediaCover ?: "No Image"
                            )
                            favoriteEventViewModel.addFavorite(favoriteEvent) // Tambahkan ke favorit
                            Toast.makeText(this, "${detailResponse.event?.name} ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                        } else {
                            favoriteButton.setImageResource(R.drawable.ic_favorite_border) // Mengubah ke ikon 'favorite_border'

                            // logika untuk menghapus event dari database favorit
                            val favoriteEvent = FavoriteEvent(
                                eventId = detailResponse.event?.id.toString(),
                                eventName = detailResponse.event?.name ?: "Unknown Event",
                                eventDescription = detailResponse.event?.description ?: "No Description",
                                imageLogo = detailResponse.event?.mediaCover ?: "No Image"
                            )
                            favoriteEventViewModel.removeFavorite(favoriteEvent) // Menghapus dari favorit
                            Toast.makeText(this, "${detailResponse.event?.name} dihapus dari favorit", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "Removing favorite event: $favoriteEvent")
                        }
                    }
                }
            }
        }, 400)
    }

    private fun checkIfFavorite(eventId: String?, favoriteButton: FloatingActionButton) {
        favoriteEventViewModel.allFavorites.observe(this) { favorites ->
            isFavorite = favorites.any { it.eventId == eventId }

            // memperbarui status tombol favorit
            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.ic_favorite) // Ganti dengan ikon 'favorite'
            } else {
                favoriteButton.setImageResource(R.drawable.ic_favorite_border) // Ganti dengan ikon 'favorite_border'
            }
        }
    }

    fun convertToSeconds(timeString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        val date = dateFormat.parse(timeString)
        return date?.time?.div(1000) ?: 0L
    }

    fun extractTime(timeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))

        // mengubah string menjadi Date
        val date = inputFormat.parse(timeString)

        // mengonversi kembali Date menjadi string dengan format jam
        return outputFormat.format(date)
    }
}
