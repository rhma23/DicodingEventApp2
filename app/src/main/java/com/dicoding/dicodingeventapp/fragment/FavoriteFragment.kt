package com.dicoding.dicodingeventapp.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingeventapp.viewModel.EventViewModel
import com.dicoding.dicodingeventapp.adapter.EventAdapterFavorite
import com.dicoding.dicodingeventapp.database.AppDatabase
import com.dicoding.dicodingeventapp.databinding.FragmentFavoriteBinding
import com.dicoding.dicodingeventapp.FavoriteEventRepository
import com.dicoding.dicodingeventapp.viewModel.FavoriteEventViewModel
import com.dicoding.dicodingeventapp.viewModel.FavoriteEventViewModelFactory

class FavoriteFragment : Fragment() {

    private lateinit var repository: FavoriteEventRepository
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels()
    private val favoriteEventViewModel: FavoriteEventViewModel by viewModels {
        FavoriteEventViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appDatabase = AppDatabase.getDatabase(requireContext())
        repository = FavoriteEventRepository(appDatabase.favoriteEventDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventAdapterFavorite()
        binding.recyclerViewFavorites.adapter = adapter
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(context)

        // menampilkan ProgressBar saat mengambil data
        binding.progressBarFav.visibility = View.VISIBLE
        binding.recyclerViewFavorites.visibility = View.INVISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            favoriteEventViewModel.allFavorites.observe(viewLifecycleOwner) { favorites ->
                if (favorites.isNullOrEmpty()) {
                    binding.recyclerViewFavorites.visibility = View.INVISIBLE
                } else {
                    adapter.submitList(favorites)
                    binding.recyclerViewFavorites.visibility = View.VISIBLE
                }
                binding.progressBarFav.visibility = View.GONE
            }
        }, 1000)

        eventViewModel.errorLiveData.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
