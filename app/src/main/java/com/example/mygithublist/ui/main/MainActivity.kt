package com.example.mygithublist.ui.main

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygithublist.R
import com.example.mygithublist.data.model.User
import com.example.mygithublist.databinding.ActivityMainBinding
import com.example.mygithublist.ui.detail.DetailUserActivity
import com.example.mygithublist.ui.favorite.FavoriteActivity
import com.example.mygithublist.ui.settings.SettingActivity
import com.example.mygithublist.ui.settings.SettingPreferences
import com.example.mygithublist.ui.settings.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: UserAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = SettingPreferences.getInstance(application.dataStore)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(preferences)
        )[MainViewModel::class.java]

        instance = this

        viewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserAdapter()
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val intent = Intent(this@MainActivity, DetailUserActivity::class.java).also {
                    it.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                    it.putExtra(DetailUserActivity.EXTRA_ID, data.id)
                    it.putExtra(DetailUserActivity.EXTRA_URL, data.avatar_url)
                }
                val mBundle: Bundle =
                    ActivityOptions.makeSceneTransitionAnimation(this@MainActivity).toBundle()
                startActivity(intent, mBundle)
            }

        })

        binding.apply {
            rvUser.layoutManager = LinearLayoutManager(this@MainActivity)
            rvUser.setHasFixedSize(true)
            rvUser.adapter = adapter
        }

        binding.btnSearch.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Default) {
                for (i in 0..10) {
                    delay(300)
                    val progress = i * 10
                    withContext(Dispatchers.Main) {
                        if (progress == 100) {
                            showLoading(false)
                            viewModel.getSearchUser().observe(this@MainActivity) {
                                if (it != null) {
                                    adapter.setList(it)
                                }
                            }
                        } else {
                            showLoading(true)
                        }
                    }
                }

            }
            searchUser()
        }
        binding.etQuery.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                searchUser()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting_menu -> {
                Intent(this@MainActivity, SettingActivity::class.java).also {
                    startActivity(it)
                }
            }

            R.id.fav_menu -> {
                Intent(this@MainActivity, FavoriteActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvUser.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun searchUser() {
        binding.apply {
            val query = etQuery.text.toString()

            if (query.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please insert Username!", Toast.LENGTH_SHORT).show()
            }
            viewModel.setSearchUser(query)
        }
    }

    companion object {
        var instance: MainActivity? = null
            private set
        val context: Context?
            get() = instance
    }
}