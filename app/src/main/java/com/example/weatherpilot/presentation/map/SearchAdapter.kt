package com.example.weatherpilot.presentation.map

import android.location.Address
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.databinding.AddressItemBinding
import com.example.weatherpilot.databinding.DayOverviewItemBinding
import com.example.weatherpilot.domain.model.DayWeatherModel

class  SearchAdapter( private val onClickAction: (Address) -> Unit) : ListAdapter<Address, SearchAdapter.ViewHolder>(
    DayDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AddressItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        val address = getItem(position)
        holder.bind(address)
    }

    inner class ViewHolder(var binding: AddressItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(address: Address) {
            binding.apply {
                binding.address = address
                binding.locationClickListener = { onClickAction(address) }
            }
        }
    }
}


class DayDiffCallback : DiffUtil.ItemCallback<Address>() {
    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem.latitude == newItem.latitude && oldItem.longitude == newItem.longitude
    }

    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
        return  oldItem.latitude == newItem.latitude && oldItem.longitude == newItem.longitude
    }
}
