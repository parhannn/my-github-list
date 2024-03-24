package com.example.mygithublist

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.mygithublist.databinding.SplashBinding
import com.example.mygithublist.ui.main.MainActivity
import com.example.mygithublist.ui.settings.SettingPreferences
import com.example.mygithublist.ui.settings.dataStore

class Splash : AppCompatActivity() {
    private lateinit var binding: SplashBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val preferences = SettingPreferences.getInstance(application.dataStore)

        viewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[SplashViewModel::class.java]

        viewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@Splash, MainActivity::class.java)
            val mBundle: Bundle =
                ActivityOptions.makeSceneTransitionAnimation(this@Splash).toBundle()
            startActivity(intent, mBundle)
            finish()
        }, duration)
    }

    companion object {
        private const val duration: Long = 3000
    }
}