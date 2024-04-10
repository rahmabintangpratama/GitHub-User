package com.dicoding.githubuser.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class FavoriteViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteAddDeleteViewModel::class.java)) {
            return FavoriteAddDeleteViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}