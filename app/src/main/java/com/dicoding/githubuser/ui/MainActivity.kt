package com.dicoding.githubuser.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.response.User
import com.dicoding.githubuser.databinding.ActivityMainBinding
import com.dicoding.githubuser.ui.viewmodel.MainViewModel
import com.dicoding.githubuser.ui.viewmodel.MainViewModelFactory
import com.dicoding.githubuser.ui.viewmodel.UserViewModel
import com.dicoding.githubuser.utils.NetworkUtils
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        adapter = UserAdapter()
        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val intent = Intent(this@MainActivity, DetailUserActivity::class.java)
                intent.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                startActivity(intent)
            }
        })

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        binding.rvUsers.adapter = adapter

        viewModel.listUser.observe(this) { users ->
            adapter.submitList(users)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
            binding.rvUsers.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    val query = searchView.text.toString().trim()
                    if (query.isNotEmpty()) {
                        viewModel.searchUser(query)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Please enter a username",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    searchBar.setText(query)
                    searchView.hide()
                    true
                }

            fabDetailFavorite.setOnClickListener {
                val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                startActivity(intent)
            }
        }

        val switchTheme = findViewById<SwitchMaterial>(R.id.switch_theme)

        val pref = SettingPreferences.getInstance(application.dataStore)
        val mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(pref))[MainViewModel::class.java]
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }

            val iconResId =
                if (isDarkModeActive) R.drawable.ic_github_user_dark else R.drawable.ic_github_user
            binding.circleImageView.setImageResource(iconResId)
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}