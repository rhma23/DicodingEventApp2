package com.dicoding.dicodingeventapp

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.dicodingeventapp.databinding.ActivityMainBinding
import com.dicoding.dicodingeventapp.viewModel.MainViewModel
import com.dicoding.dicodingeventapp.viewModel.ViewModelFactory
import android.view.View
import com.dicoding.dicodingeventapp.fragment.FavoriteFragment
import com.dicoding.dicodingeventapp.fragment.FinishedFragment
import com.dicoding.dicodingeventapp.fragment.HomeFragment
import com.dicoding.dicodingeventapp.fragment.SettingFragment
import com.dicoding.dicodingeventapp.fragment.UpcomingFragment
import com.dicoding.dicodingeventapp.fragment.dataStore
import com.dicoding.dicodingeventapp.viewModel.EventViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val eventViewModel: EventViewModel by viewModels()
    private lateinit var mainViewModel: MainViewModel
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        // inisialisasi SettingPreferences dan MainViewModel sebelum setContentView
        val pref = SettingPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        // observe tema dan set tema sesuai dengan preferensi yang tersimpan
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup Bottom Navigation
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.upcoming -> {
                    loadFragment(UpcomingFragment())
                    binding.searchView.visibility = View.VISIBLE
                    eventViewModel.fetchEventsUpcoming(active = 1)

                    binding.searchView.setOnQueryTextListener(object :
                        SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            query?.let {
                                eventViewModel.isLoading.postValue(true)
                                eventViewModel.searchEventsUpcoming(it)
                                Log.d("SearchView", "Query submitted on search upcoming: $it")
                            }
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            newText?.let {
                                Log.d("SearchView", "Query text changed on search finish: $it")
                            }
                            return false
                        }
                    })

                    resetDataUpcoming()

                    true
                }

                R.id.finished -> {
                    loadFragment(FinishedFragment())
                    binding.searchView.visibility = View.VISIBLE
                    eventViewModel.fetchEventsFinish(active = 0)

                    binding.searchView.setOnQueryTextListener(object :
                        SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            query?.let {
                                eventViewModel.isLoading.postValue(true)
                                eventViewModel.searchEventsFinish(it)
                                Log.d("SearchView", "Query submitted on search finish: $it")
                            }
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            newText?.let {
                                Log.d("SearchView", "Query text changed on search finish: $it")
                            }
                            return false
                        }
                    })
                    resetDataFinish()
                    true
                }

                R.id.favorite -> {
                    loadFragment(FavoriteFragment())
                    binding.searchView.visibility = View.GONE // Atur tampilan search jika perlu
                    true
                }

                R.id.home -> {
                    loadFragment(HomeFragment())
                    binding.searchView.visibility = View.GONE
                    true
                }

                R.id.setting -> {
                    loadFragment(SettingFragment())
                    binding.searchView.visibility = View.GONE // Sembunyikan SearchView jika perlu
                    true
                }

                else -> false
            }
        }

        // setup SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    eventViewModel.isLoading.postValue(true)
                    eventViewModel.searchEventsUpcoming(it)
                    Log.d("SearchView", "Query submitted: $it")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    Log.d("SearchView", "Query text changed: $it")
                }
                return false
            }
        })

        // load default fragment
        if (savedInstanceState == null) {
            loadFragment(UpcomingFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun resetDataFinish() {
        binding.searchView.setQuery("", false) // menghapus query dari SearchView
        eventViewModel.fetchEventsFinish(active = 0) // memuat ulang data asli
    }

    private fun resetDataUpcoming() {
        binding.searchView.setQuery("", false) // menghapus query dari SearchView
        eventViewModel.fetchEventsFinish(active = 1) // memuat ulang data asli
    }

    fun setFragmentContainerVisibility(visibility: Int) {
        binding.fragmentContainer.visibility = visibility
    }
}
