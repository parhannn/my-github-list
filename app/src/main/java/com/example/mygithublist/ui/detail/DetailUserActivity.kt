package com.example.mygithublist.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.mygithublist.R
import com.example.mygithublist.databinding.ActivityDetailUserBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var viewModel: DetailUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        val id = intent.getIntExtra(EXTRA_ID, 0)
        val avatarUrl = intent.getStringExtra(EXTRA_URL)
        val mBundle = Bundle()
        val sectionPagerAdapter = SectionPagerAdapter(this, mBundle)
        val handler = Handler(Looper.getMainLooper())
        val executors = Executors.newSingleThreadExecutor()

        mBundle.putString(EXTRA_USERNAME, username)

        viewModel = ViewModelProvider(this)[DetailUserViewModel::class.java]

        viewModel.setUserDetail(username)

        executors.execute {
            try {
                for (i in 0..10) {
                    Thread.sleep(500)
                    val progress = i * 10
                    handler.post {
                        if (progress == 100) {
                            showLoading(false)
                        } else {
                            showLoading(true)
                        }
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        viewModel.getUserDetail().observe(this) {
            if (it != null) {
                binding.apply {
                    tvName.text = it.name
                    tvUsername.text = it.login
                    tvFollowers.text = StringBuilder(it.followers.toString()).append(" Followers")
                    tvFollowing.text = StringBuilder(it.following.toString()).append(" Following")
                    Glide.with(this@DetailUserActivity).load(it.avatar_url)
                        .transition(DrawableTransitionOptions.withCrossFade()).centerCrop()
                        .into(ivProfile)
                }
            } else {
                binding.tvName.text = NO_NAME
            }
        }

        var _isChecked = false
        CoroutineScope(Dispatchers.IO).launch {
            val count = viewModel.checkUser(id)
            withContext(Dispatchers.Main) {
                if (count != null) {
                    _isChecked = if (count > 0) {
                        binding.fabFavorite.setImageResource(R.drawable.ic_favorited)
                        true
                    } else {
                        binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
                        false
                    }
                }
            }
        }

        binding.fabFavorite.setOnClickListener {
            _isChecked = !_isChecked
            if (_isChecked) {
                if (username != null && avatarUrl != null) {
                    viewModel.addToFavorite(username, id, avatarUrl)
                    binding.fabFavorite.setImageResource(R.drawable.ic_favorited)
                }
            } else {
                viewModel.removeFromFavorite(id)
                binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
            }
        }

        binding.apply {
            viewPager.adapter = sectionPagerAdapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.profileProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.ivProfile.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvName.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvUsername.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.linearLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_URL = "extra_url"
        const val NO_NAME = "User doesn't have name"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_1, R.string.tab_2
        )
    }
}