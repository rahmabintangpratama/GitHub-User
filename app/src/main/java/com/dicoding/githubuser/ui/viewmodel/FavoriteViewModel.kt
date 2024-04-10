package com.dicoding.githubuser.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.githubuser.data.database.Favorite
import com.dicoding.githubuser.data.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavoriteRepository = FavoriteRepository(application)
    val favoriteList: LiveData<List<Favorite>> = repository.getAllFavorite()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
}
