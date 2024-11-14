package com.dicoding.dicodingeventapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingeventapp.databinding.FragmentUpcomingBinding
import com.google.gson.annotations.SerializedName

data class DetailResponse(
	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("event")
	val event: Event? = null
)

data class Event(
	@field:SerializedName("summary")
	val summary: String? = null,

	@field:SerializedName("mediaCover")
	val mediaCover: String? = null,

	@field:SerializedName("registrants")
	val registrants: Int = 0, // Default ke 0

	@field:SerializedName("imageLogo")
	val imageLogo: String? = null,

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("ownerName")
	val ownerName: String? = null,

	@field:SerializedName("cityName")
	val cityName: String? = null,

	@field:SerializedName("quota")
	val quota: Int = 0, // Default ke 0

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("beginTime")
	val beginTime: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null,

	@field:SerializedName("category")
	val category: String? = null
) {
	val active: Int? = null
}
