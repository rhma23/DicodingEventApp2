package com.dicoding.dicodingeventapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dicoding.dicodingeventapp.databinding.FragmentFinishedBinding
import android.os.Handler
import android.os.Looper
import com.dicoding.dicodingeventapp.DetailEventActivity
import com.dicoding.dicodingeventapp.adapter.EventAdapter
import com.dicoding.dicodingeventapp.viewModel.EventViewModel

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by activityViewModels()
    private lateinit var adapter: EventAdapter
    private val TAG = "FinishedFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventAdapter(emptyList()) { event ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            Log.d(TAG, "onViewCreated Finish Fragment: $event.id")
            intent.putExtra("id", event.id)
            startActivity(intent)
        }

        // menampilkan ProgressBar saat mengambil data
        binding.progressBarFinished.visibility = View.VISIBLE
        eventViewModel.fetchEventsFinish(active = 0)

        // setup RecyclerView
        binding.recyclerViewFinished.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerViewFinished.adapter = adapter

        // observe status loading
        Handler(Looper.getMainLooper()).postDelayed({
            eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    binding.progressBarFinished.visibility = View.VISIBLE // tampilkan ProgressBar
                    binding.recyclerViewFinished.visibility = View.INVISIBLE // sembunyikan RecyclerView sementara
                    binding.textViewErrorFinished.visibility = View.INVISIBLE
                } else {
                    binding.progressBarFinished.visibility = View.INVISIBLE // sembunyikan ProgressBar
                    binding.recyclerViewFinished.visibility = View.VISIBLE // tampilkan RecyclerView
                    binding.textViewErrorFinished.visibility = View.VISIBLE
                }
            }
        }, 1000)


        Handler(Looper.getMainLooper()).postDelayed({
            // observe event data
            viewLifecycleOwnerLiveData.observe(viewLifecycleOwner) { viewLifecycleOwner ->
                eventViewModel.eventsLiveDataFinished.observe(viewLifecycleOwner) { events ->
                    binding.progressBarFinished.visibility = View.INVISIBLE
                    if (events != null && events.isNotEmpty()) {
                        adapter.updateData(events)
                        binding.textViewErrorFinished.visibility = View.INVISIBLE
                        binding.recyclerViewFinished.visibility = View.VISIBLE
                    } else {
                        Log.e(TAG, "Event search  data  finish is null or empty")
                        binding.textViewErrorFinished.visibility = View.VISIBLE
                        binding.recyclerViewFinished.visibility = View.INVISIBLE
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
