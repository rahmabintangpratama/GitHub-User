package com.dicoding.githubuser.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.database.Favorite
import com.dicoding.githubuser.databinding.ItemUserBinding

class FavoriteAdapter(private val context: Context) :
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var favoriteList = listOf<Favorite>()

    inner class FavoriteViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: Favorite) {
            with(binding) {
                tvUsername.text = favorite.login
                tvHtml.text = context.getString(R.string.html, favorite.htmlUrl)
                Glide.with(itemView.context)
                    .load(favorite.avatarUrl)
                    .centerCrop()
                    .into(ivUserAvatar)
                itemView.setOnClickListener {
                    val intent = Intent(context, DetailUserActivity::class.java)
                    intent.putExtra(DetailUserActivity.EXTRA_USERNAME, favorite.login)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favoriteList[position]
        holder.bind(favorite)
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFavoriteList(favorites: List<Favorite>) {
        favoriteList = favorites
        notifyDataSetChanged()
    }
}