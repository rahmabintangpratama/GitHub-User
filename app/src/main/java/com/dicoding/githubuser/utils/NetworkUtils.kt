@file:Suppress("DEPRECATION")

package com.dicoding.githubuser.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
