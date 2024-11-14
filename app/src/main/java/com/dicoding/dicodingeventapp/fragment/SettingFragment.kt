package com.dicoding.dicodingeventapp.fragment

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dicoding.dicodingeventapp.R
import com.dicoding.dicodingeventapp.MyWorker
import com.dicoding.dicodingeventapp.viewModel.MainViewModel
import com.dicoding.dicodingeventapp.SettingPreferences
import com.dicoding.dicodingeventapp.viewModel.ViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.concurrent.TimeUnit

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private var isClick = false
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
        val switchTheme = view.findViewById<SwitchMaterial>(R.id.switchTheme)
        val reminder = view.findViewById<SwitchMaterial>(R.id.switchThemeReminder)

        preferences = requireContext().getSharedPreferences("activity_setting", Context.MODE_PRIVATE)
        val isReminderEnabled = preferences.getBoolean("switchThemeReminder", false)
        reminder.isChecked = isReminderEnabled

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
            mainViewModel.getThemeSettings().observe(viewLifecycleOwner) {
            }
        }

        reminder.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            val editor = preferences.edit()
            editor.putBoolean("switchThemeReminder", isChecked)
            editor.apply()

            isClick = isChecked

            setupDailyReminder(isClick)
        }
    }

    private fun setupDailyReminder(isEnabled: Boolean) {
        val workManager = WorkManager.getInstance(requireContext())
        if (isEnabled) {
            val workRequest = PeriodicWorkRequestBuilder<MyWorker>(1, TimeUnit.DAYS)
                .build()
            workManager.enqueueUniquePeriodicWork(
                "dailyReminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        } else {
            workManager.cancelUniqueWork("dailyReminder")
        }
    }
}
