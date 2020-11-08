package com.m.ginwa.dicoding.mygithubre.ui.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.utils.RecyclerDeleteListener
import com.m.ginwa.dicoding.mygithubre.utils.RecyclerViewClickListener
import kotlinx.android.synthetic.main.list_user_favorite.view.*
import javax.inject.Inject

class FavoriteAdapter @Inject constructor() :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    private var recentDeleteDataPosition: Int? = null
    var recentDeleteData: User? = null
    var dataSet = arrayListOf<User>()
    private var deleteListener: RecyclerDeleteListener? = null
    private var clickListener: RecyclerViewClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_user_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.load()
    }

    fun setOnRecyclerViewDeleteListener(listener: RecyclerDeleteListener) {
        deleteListener = listener
    }

    fun setOnRecyclerViewClick(listener: RecyclerViewClickListener) {
        clickListener = listener
    }

    fun updateDataSet(dataSet: List<User>?) {
        if (this.dataSet != dataSet && dataSet != null) {
            this.dataSet.clear()
            this.dataSet.addAll(dataSet)
            notifyDataSetChanged()
        }
    }

    fun deleteData(adapterPosition: Int) {
        recentDeleteData = dataSet[adapterPosition]
        recentDeleteDataPosition = adapterPosition
        dataSet.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
        deleteListener?.onDelete(recentDeleteDataPosition, recentDeleteData)
    }

    fun updateData(user: User?) {
        user?.let {
            dataSet.add(user)
            notifyItemInserted(dataSet.lastIndex)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun load() {
            dataSet[adapterPosition].let {
                itemView.apply {
                    Glide.with(context)
                        .load(dataSet[adapterPosition].avatarUrl)
                        .apply {
                            transform(CenterCrop(), RoundedCorners(8))
                            override(60, 60)
                        }
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageUser)

                    textLogin.text = it.login
                    textLocation.text = it.location
                    textCompany.text = it.company
                    if (!textLocation.text.isNullOrEmpty()) textLocation.visibility = VISIBLE
                    else textLocation.visibility = GONE

                    if (!textCompany.text.isNullOrEmpty()) textCompany.visibility = VISIBLE
                    else textCompany.visibility = GONE
                }

                val bundle = bundleOf("username" to it.login)
                itemView.container.setOnClickListener { clickListener?.onClick(bundle) }
            }
        }
    }
}
