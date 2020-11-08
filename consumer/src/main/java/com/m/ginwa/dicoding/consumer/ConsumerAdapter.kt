package com.m.ginwa.dicoding.consumer

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.list_user_favorite.view.*

class ConsumerAdapter :
    RecyclerView.Adapter<ConsumerAdapter.ViewHolder>() {
    var dataSet = arrayListOf<User>()


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

    fun updateDataSet(dataSet: List<User>?) {
        if (this.dataSet != dataSet && dataSet != null) {
            this.dataSet.clear()
            this.dataSet.addAll(dataSet)
            notifyDataSetChanged()
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
            }
        }
    }
}
