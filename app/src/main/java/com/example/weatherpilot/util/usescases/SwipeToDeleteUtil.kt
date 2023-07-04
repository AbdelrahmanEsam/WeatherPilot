package com.example.weatherpilot.util.usescases

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

fun RecyclerView.swipeRecyclerItemListener( action  : (ViewHolder) -> Unit)
{
    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: ViewHolder,
            target: ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
            action.invoke(viewHolder)
        }

    }).attachToRecyclerView(this)
}