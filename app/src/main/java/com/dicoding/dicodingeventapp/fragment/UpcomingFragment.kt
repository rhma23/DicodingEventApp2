package com.dicoding.dicodingeventapp.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingeventapp.DetailEventActivity
import com.dicoding.dicodingeventapp.adapter.EventAdapter
import com.dicoding.dicodingeventapp.viewModel.EventViewModel
import com.dicoding.dicodingeventapp.databinding.FragmentUpcomingBinding

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by activityViewModels()
    private val TAG = "UpcomingFragment"
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventAdapter = EventAdapter(emptyList()) { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra("id", event.id)
            startActivity(intent)
        }

        binding.recyclerViewUpcoming.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUpcoming.adapter = eventAdapter

        eventViewModel.fetchEventsUpcoming(active = 1)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded && view != null) {
                eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                    if (isLoading) {
                        binding.progressBarUpcoming.visibility = View.VISIBLE
                        binding.recyclerViewUpcoming.visibility = View.INVISIBLE
                        binding.textViewErrorUpcoming.visibility = View.INVISIBLE
                    } else {
                        binding.progressBarUpcoming.visibility = View.INVISIBLE
                        binding.recyclerViewUpcoming.visibility = View.VISIBLE
                        binding.textViewErrorUpcoming.visibility = View.VISIBLE
                    }
                }
            }
        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded && view != null) {
                eventViewModel.eventsLiveDataUpcoming.observe(viewLifecycleOwner) { events ->
                    binding.progressBarUpcoming.visibility = View.INVISIBLE
                    if (events != null && events.isNotEmpty()) {
                        eventAdapter.updateData(events)
                        binding.textViewErrorUpcoming.visibility = View.GONE
                        binding.recyclerViewUpcoming.visibility = View.VISIBLE
                    } else {
                        Log.e(TAG, "Event search data finish is null or empty")
                        binding.textViewErrorUpcoming.visibility = View.VISIBLE
                        binding.recyclerViewUpcoming.visibility = View.INVISIBLE
                    }
                }
            }
        }, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
