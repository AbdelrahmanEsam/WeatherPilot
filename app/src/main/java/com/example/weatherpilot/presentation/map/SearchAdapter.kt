package com.example.weatherpilot.presentation.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.databinding.SearchItemBinding
import com.example.weatherpilot.domain.model.SearchItem

class  SearchAdapter( private val onClickAction: (SearchItem) -> Unit) : ListAdapter<SearchItem, SearchAdapter.ViewHolder>(
    DayDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SearchItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        val address = getItem(position)
        holder.bind(address)
    }

    inner class ViewHolder(var binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(searchItem: SearchItem) {
            binding.apply {
                binding.searchItem = searchItem
                binding.locationClickListener = { onClickAction(searchItem) }
            }
        }
    }
}


class DayDiffCallback : DiffUtil.ItemCallback<SearchItem>() {
    override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
        return oldItem.lat == newItem.lon && oldItem.lat == newItem.lon
    }

    override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
        return oldItem.lat == newItem.lon && oldItem.lat == newItem.lon
    }
}
