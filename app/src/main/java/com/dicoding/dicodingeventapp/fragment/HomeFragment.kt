package com.dicoding.dicodingeventapp.fragment

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingeventapp.databinding.FragmentHomeBinding
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.dicodingeventapp.DetailEventActivity
import com.dicoding.dicodingeventapp.adapter.EventAdapterFinished
import com.dicoding.dicodingeventapp.adapter.EventAdapterUpcoming
import com.dicoding.dicodingeventapp.viewModel.EventViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var upcomingAdapter: EventAdapterUpcoming
    private lateinit var finishedAdapter: EventAdapterFinished

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        binding.progressBarHome.visibility = View.VISIBLE

        finishedAdapter = EventAdapterFinished(emptyList()) { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra("id", event.id)
            startActivity(intent)
        }

        upcomingAdapter = EventAdapterUpcoming(emptyList()) { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra("id", event.id)
            startActivity(intent)
        }

        setupRecyclerViews()

        eventViewModel.limitEventsUpcoming(1, 5)// untuk event yang akan datang
        eventViewModel.limitEventsFinished(0, 5) // untuk event yang sudah selesai

        Handler(Looper.getMainLooper()).postDelayed({
        eventViewModel.eventsLiveDataUpcoming.observe(viewLifecycleOwner) { events ->
            events?.let {
                if (events.isNotEmpty()) {
                    binding.progressBarHome.visibility = View.INVISIBLE
                    upcomingAdapter.updateData(events)
                } else {
                    Log.e(TAG, "Event data is null or empty")
                }

                }
            }
        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed({
            eventViewModel.eventsLiveDataFinished.observe(viewLifecycleOwner) { events ->
                events?.let {
                    binding.progressBarHome.visibility = View.INVISIBLE
                    finishedAdapter.updateData(events)
                }
            }
        }, 1000)

        eventViewModel.errorLiveData.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerViews() {

        upcomingAdapter = EventAdapterUpcoming  (listOf()) { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra("id", event.id)
            startActivity(intent)
        }
        binding.rvUpcoming.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        // setup upcoming events RecyclerView (Horizontal)
        finishedAdapter = EventAdapterFinished(listOf()) { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra("id", event.id)
            startActivity(intent)
        }
        binding.rvFinished.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = finishedAdapter
        }
    }
}
