package com.m.ginwa.dicoding.mygithubre.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.m.ginwa.dicoding.mygithubre.ui.favorite.FavoriteAdapter


class SwipeToDelete<T>(private val adapter: T) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (adapter is FavoriteAdapter) {
            adapter.deleteData(viewHolder.adapterPosition)
        }
    }
}