package com.dicoding.githubuser.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.data.database.Favorite
import com.dicoding.githubuser.data.repository.FavoriteRepository

class FavoriteAddDeleteViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    fun insert(favorite: Favorite) {
        mFavoriteRepository.insert(favorite)
    }

    fun delete(favorite: Favorite) {
        mFavoriteRepository.delete(favorite)
    }

    fun isFavorite(login: String): LiveData<Boolean> {
        return mFavoriteRepository.isFavorite(login)
    }
}