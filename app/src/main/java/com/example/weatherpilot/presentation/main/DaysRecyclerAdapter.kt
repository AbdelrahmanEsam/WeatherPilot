package com.example.weatherpilot.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.databinding.DayOverviewItemBinding
import com.example.weatherpilot.domain.model.DayWeatherModel

class DaysRecyclerAdapter(private val onClickAction: (Int) -> Unit) : ListAdapter<DayWeatherModel, DaysRecyclerAdapter.ViewHolder>(
    DayDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DayOverviewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: DaysRecyclerAdapter.ViewHolder, position: Int) {
        val day = getItem(position)
        holder.bind(day)
    }

    inner class ViewHolder(var binding: DayOverviewItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(day: DayWeatherModel) {
            binding.apply {
                this.day = day
            }
        }
    }
}


class DayDiffCallback : DiffUtil.ItemCallback<DayWeatherModel>() {
    override fun areItemsTheSame(oldItem: DayWeatherModel, newItem: DayWeatherModel): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: DayWeatherModel, newItem: DayWeatherModel): Boolean {
        return  oldItem == newItem
    }
}
