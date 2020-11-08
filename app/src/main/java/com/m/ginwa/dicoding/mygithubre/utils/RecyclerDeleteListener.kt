package com.m.ginwa.dicoding.mygithubre.utils

import com.m.ginwa.dicoding.mygithubre.data.model.User

interface RecyclerDeleteListener {
    fun onDelete(adapterPosition: Int?, user: User?)
}