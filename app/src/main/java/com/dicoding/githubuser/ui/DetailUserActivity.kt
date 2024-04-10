package com.dicoding.githubuser.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.database.Favorite
import com.dicoding.githubuser.data.response.DetailUser
import com.dicoding.githubuser.databinding.ActivityDetailUserBinding
import com.dicoding.githubuser.ui.viewmodel.DetailUserViewModel
import com.dicoding.githubuser.ui.viewmodel.FavoriteAddDeleteViewModel
import com.dicoding.githubuser.ui.viewmodel.FavoriteViewModelFactory
import com.dicoding.githubuser.ui.viewmodel.MainViewModel
import com.dicoding.githubuser.ui.viewmodel.MainViewModelFactory
import com.dicoding.githubuser.utils.NetworkUtils
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private val viewModel: DetailUserViewModel by viewModels()
    private var isFavorite: Boolean = false
    private val favoriteViewModel: FavoriteAddDeleteViewModel by viewModels {
        FavoriteViewModelFactory(
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val username = intent.getStringExtra(EXTRA_USERNAME)
        Log.d("DetailUserActivity", "Username from intent: $username")
        if (username == null) {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.viewUserDetail(username)

        val sectionPagerAdapter = SectionPagerAdapter(this, username)
        binding.viewPagerDetailUser.adapter = sectionPagerAdapter

        val tabTitles =
            arrayOf(getString(R.string.tab_followers), getString(R.string.tab_following))
        TabLayoutMediator(
            binding.tabLayoutDetailUser,
            binding.viewPagerDetailUser
        ) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        observeViewModel()
        binding.fabFavorite.setOnClickListener {
            if (isFavorite) {
                deleteFavorite()
            } else {
                insertFavorite()
            }
        }

        binding.fabShare.setOnClickListener {
            shareUserDetails(viewModel.user.value)
        }

        viewModel.user.observe(this) { detailUser ->
            bindUserDetails(detailUser)
            observeFavoriteStatus(detailUser.login)
        }

        getAppTheme()
    }

    private fun observeViewModel() {
        viewModel.user.observe(this) { detailUser ->
            bindUserDetails(detailUser)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindUserDetails(detailUser: DetailUser) {
        binding.apply {
            if (detailUser.name.isNullOrEmpty()) {
                tvProfileName.text = getString(R.string.name_not_specified)
            } else {
                tvProfileName.text = detailUser.name
            }
            tvProfileUsername.text = detailUser.login
            tvFollowers.text = getString(R.string.followers_count, detailUser.followers)
            tvFollowing.text = getString(R.string.following_count, detailUser.following)
            Glide.with(this@DetailUserActivity)
                .load(detailUser.avatarUrl)
                .into(ivProfileAvatar)
        }
    }

    private fun observeFavoriteStatus(login: String) {
        favoriteViewModel.isFavorite(login).observe(this) { isFav ->
            isFavorite = isFav
            updateFavoriteIcon()
        }
    }

    private fun insertFavorite() {
        val detailUser = viewModel.user.value
        if (detailUser != null) {
            val favorite = Favorite(
                id = detailUser.id,
                login = detailUser.login,
                avatarUrl = detailUser.avatarUrl,
                htmlUrl = detailUser.htmlUrl
            )
            favoriteViewModel.insert(favorite)
            Snackbar.make(binding.root, "Added to favorites", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun deleteFavorite() {
        val detailUser = viewModel.user.value
        if (detailUser != null) {
            val favorite = Favorite(
                id = detailUser.id,
                login = detailUser.login,
                avatarUrl = detailUser.avatarUrl,
                htmlUrl = detailUser.htmlUrl
            )
            favoriteViewModel.delete(favorite)
            Snackbar.make(binding.root, "Removed from favorites", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateFavoriteIcon() {
        val iconResId = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, iconResId))
    }

    private fun getAppTheme() {
        val pref = SettingPreferences.getInstance(dataStore)
        val viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(pref)
        )[MainViewModel::class.java]
        viewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            val tvFollowers = findViewById<TextView>(R.id.tvFollowers)
            val tvFollowing = findViewById<TextView>(R.id.tvFollowing)

            val drawableResId =
                if (isDarkModeActive) R.drawable.ic_people_dark else R.drawable.ic_people

            tvFollowers.setCompoundDrawablesWithIntrinsicBounds(drawableResId, 0, 0, 0)
            tvFollowing.setCompoundDrawablesWithIntrinsicBounds(drawableResId, 0, 0, 0)
        }
    }

    private fun shareUserDetails(detailUser: DetailUser?) {
        val shareText = detailUser?.htmlUrl ?: "Check out this GitHub user's profile!"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        startActivity(Intent.createChooser(shareIntent, "Share GitHub Profile"))
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }
}