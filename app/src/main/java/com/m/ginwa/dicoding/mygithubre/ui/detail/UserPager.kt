package com.m.ginwa.dicoding.mygithubre.ui.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.m.ginwa.dicoding.mygithubre.ui.detail.followers.FollowersFragment
import com.m.ginwa.dicoding.mygithubre.ui.detail.following.FollowingFragment

class UserPager(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BioFragment.newInstance()
            1 -> FollowersFragment.newInstance()
            2 -> FollowingFragment.newInstance()
            else -> Fragment()
        }
    }
}