package com.example.weatherpilot.presentation.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.databinding.AlertItemBinding
import com.example.weatherpilot.domain.model.AlertItem

class AlertsRecyclerAdapter(
    private val onClickAction: (Int) -> Unit
) : ListAdapter<AlertItem, AlertsRecyclerAdapter.ViewHolder>(
    DayDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AlertItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AlertsRecyclerAdapter.ViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
    }

    inner class ViewHolder(var binding: AlertItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(location: AlertItem) {
            binding.apply {
                this.alert = location
                this.alertClickListener = {onClickAction(adapterPosition)}
            }
        }
    }
}


class DayDiffCallback : DiffUtil.ItemCallback<AlertItem>() {
    override fun areItemsTheSame(oldItem: AlertItem, newItem: AlertItem): Boolean {
        return oldItem.latitude == newItem.latitude && oldItem.longitude == newItem.longitude
    }

    override fun areContentsTheSame(oldItem: AlertItem, newItem: AlertItem): Boolean {
        return oldItem == newItem
    }
}
