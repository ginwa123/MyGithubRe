package com.m.ginwa.dicoding.mygithubre.ui.detail.following

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.model.Following
import com.m.ginwa.dicoding.mygithubre.utils.RecyclerViewClickListener
import kotlinx.android.synthetic.main.list_user.view.*

class FollowingAdapter(DIFF_CALLBACK: DiffUtil.ItemCallback<Following>) :
    PagedListAdapter<Following, FollowingAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var clickListener: RecyclerViewClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_user, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.load()
    }

    fun setOnRecyclerViewClick(listener: RecyclerViewClickListener) {
        clickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun load() {
            getItem(adapterPosition)?.let {
                itemView.apply {
                    Glide.with(context)
                        .load(getItem(adapterPosition)?.avatarUrl)
                        .apply {
                            transform(CenterCrop(), RoundedCorners(8))
                            override(60, 60)
                        }
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageUser)

                    textLogin.text = it.login
                }

                val bundle = bundleOf("username" to it.login)
                itemView.container.setOnClickListener { clickListener?.onClick(bundle) }
            }
        }
    }
}