package com.example.weatherpilot.presentation.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.databinding.DayOverviewItemBinding

//class RecyclerAdapter(private val onClickAction: (Int) -> Unit) : ListAdapter<Product, RecyclerAdapter.ViewHolder>(
//    ProductDiffCallback()
//) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(DayOverviewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//       val product = getItem(position)
//       holder.bind(product)
//    }
//
//    inner class ViewHolder(var binding: DayOverviewItemBinding) : RecyclerView.ViewHolder(binding.root)
//    {
//        fun bind(product: Product) {
//            binding.apply {
//                Log.d("product",product.toString())
//                this.product = product
//               lambda = { onClickAction(adapterPosition) }
//            }
//        }
//    }
//}
//
//
//class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
//    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
//        return oldItem.id == newItem.id
//    }
//
//    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
//     return  oldItem == newItem
//    }
//}


