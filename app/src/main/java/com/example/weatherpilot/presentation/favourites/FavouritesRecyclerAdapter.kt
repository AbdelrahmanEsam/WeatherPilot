package com.example.weatherpilot.presentation.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.databinding.FavouriteItemBinding
import com.example.weatherpilot.domain.model.Location

class FavouritesRecyclerAdapter(
    private val onClickAction: (Int) -> Unit
) : ListAdapter<Location, FavouritesRecyclerAdapter.ViewHolder>(
    DayDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FavouriteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavouritesRecyclerAdapter.ViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
    }

    inner class ViewHolder(var binding: FavouriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(location: Location) {
            binding.apply {
                this.location = location
                this.favouriteClickListener = {onClickAction(adapterPosition)}
            }
        }
    }
}


class DayDiffCallback : DiffUtil.ItemCallback<Location>() {
    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.latitude == newItem.latitude && oldItem.longitude == newItem.longitude
    }

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem == newItem
    }
}
