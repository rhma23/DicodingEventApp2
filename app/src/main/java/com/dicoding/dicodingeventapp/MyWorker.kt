package com.dicoding.dicodingeventapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.SyncHttpClient
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.util.Locale

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object {
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun doWork(): Result {
        // mengecek apakah pengingat diaktifkan oleh pengguna
        val preferences = applicationContext.getSharedPreferences("activity_setting", Context.MODE_PRIVATE)
        val isReminderEnabled = preferences.getBoolean("switchThemeReminder", false)

        if (isReminderEnabled) {
            // jika pengingat aktif, ambil event dan tampilkan notifikasi
            Log.d(TAG, "Reminder is enabled, fetching event data.")
            createNotificationChannel()
            fetchEventDataAndShowNotification()
        } else {
            Log.d(TAG, "Reminder is not enabled.")
        }
        return Result.success()
    }

    private fun fetchEventDataAndShowNotification() {
        val client = SyncHttpClient()
        client.get("https://event-api.dicoding.dev/events?active=1&limit=5", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseBody: ByteArray?) {
                val result = responseBody?.let { String(it) }
                Log.d(TAG, "Response received: $result")

                try {
                    val jsonObject = result?.let { JSONObject(it) }
                    val eventArray = jsonObject?.getJSONArray("listEvents")

                    if (eventArray != null && eventArray.length() > 0) {
                        // mengonversi JSONArray menjadi List<JSONObject>
                        val events = mutableListOf<JSONObject>()
                        for (i in 0 until eventArray.length()) {
                            eventArray.getJSONObject(i)?.let { events.add(it) }
                        }

                        // mengurutkan event berdasarkan beginTime secara menurun (descending)
                        val sortedEvents = events.sortedBy {
                            it.optString("beginTime")
                        }

                        // mengambil event pertama dari hasil yang telah diurutkan
                        val event = sortedEvents[0]
                        val eventName = event.getString("name")
                        val eventTime = formatDate(event.optString("beginTime"))

                        // menampilkan notifikasi
                        showNotification("Recommendation event for you on $eventTime", eventName)
                    } else {
                        Log.d(TAG, "No events found in the response.")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing event data", e)
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseBody: ByteArray?, error: Throwable?) {
                Log.e(TAG, "Failed to fetch event. Status code: $statusCode", error)
                responseBody?.let {
                    val errorResponse = String(it)
                    Log.e(TAG, "Error response body: $errorResponse")
                }
            }
        })
    }

    private fun showNotification(title: String, description: String?) {
        Log.d(TAG, "Showing notification: Title: $title, Description: $description")

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                this.description = "Channel for event notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)

        Log.d(TAG, "Notification posted with ID: $NOTIFICATION_ID")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for event notifications"
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun formatDate(dateString: String): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat("MMM d", Locale("id", "ID"))
        val date = originalFormat.parse(dateString)

        val formattedDate = date?.let { targetFormat.format(it) } ?: ""
        return if (formattedDate.isNotEmpty()) {
            val day = formattedDate.split(" ")[1].toInt()
            val suffix = when (day) {
                1, 21, 31 -> "st"
                2, 22 -> "nd"
                3, 23 -> "rd"
                else -> "th"
            }
            "${formattedDate.split(" ")[0]} ${day}${suffix}"
        } else {
            ""
        }
    }
}
