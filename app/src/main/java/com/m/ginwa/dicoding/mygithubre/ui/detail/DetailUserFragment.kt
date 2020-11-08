package com.m.ginwa.dicoding.mygithubre.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.ui.ActivityViewModel
import com.m.ginwa.dicoding.mygithubre.utils.enforceSingleScrollDirection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_detail_user.*

@AndroidEntryPoint
class DetailUserFragment : Fragment(R.layout.fragment_detail_user) {

    private lateinit var viewPagerAdapter: UserPager
    private val detailViewModel: DetailViewModel by navGraphViewModels(R.id.nav_detail_graph) {
        defaultViewModelProviderFactory
    }
    private val activityViewModel: ActivityViewModel by activityViewModels()
    private val ViewPager2.recyclerView: RecyclerView
        get() {
            return this[0] as RecyclerView
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply { detailViewModel.login = getString("username") }
        setViewPager()
    }

    private fun setViewPager() {
        viewPagerAdapter = UserPager(childFragmentManager, lifecycle)
        viewPager.recyclerView.enforceSingleScrollDirection()
        viewPager.apply {
            adapter = viewPagerAdapter
            offscreenPageLimit = 3
        }
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.bio)
                1 -> tab.text = getString(R.string.followers)
                2 -> tab.text = getString(R.string.following)
            }
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                activityViewModel.toolbarListener.value = position == 0
            }
        })

    }

    override fun onDestroyView() {
        activityViewModel.toolbarListener.value = false
        activityViewModel.progressBarLive.value = false
        activityViewModel.imageToolbarListener.value = null
        activityViewModel.fabIconListener.value = null
        super.onDestroyView()
    }
}