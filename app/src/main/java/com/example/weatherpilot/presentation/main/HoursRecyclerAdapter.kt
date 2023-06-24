package com.example.weatherpilot.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.databinding.HourOverviewItemBinding
import com.example.weatherpilot.domain.model.HourWeatherModel

class HoursRecyclerAdapter(private val onClickAction: (Int) -> Unit) : ListAdapter<HourWeatherModel, HoursRecyclerAdapter.ViewHolder>(
    HourDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HourOverviewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hour = getItem(position)
        holder.bind(hour)

    }

    inner class ViewHolder(var binding: HourOverviewItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(hour: HourWeatherModel) {
            binding.apply {
                this.hour = hour
            }
        }
    }
}


class HourDiffCallback : DiffUtil.ItemCallback<HourWeatherModel>() {
    override fun areItemsTheSame(oldItem: HourWeatherModel, newItem: HourWeatherModel): Boolean {
        return oldItem.timestamp == newItem.timestamp
    }

    override fun areContentsTheSame(oldItem: HourWeatherModel, newItem: HourWeatherModel): Boolean {
        return  oldItem == newItem
    }
}
