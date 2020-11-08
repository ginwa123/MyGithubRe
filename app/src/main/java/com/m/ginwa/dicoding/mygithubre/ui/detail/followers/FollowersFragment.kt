package com.m.ginwa.dicoding.mygithubre.ui.detail.followers

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.ui.ActivityViewModel
import com.m.ginwa.dicoding.mygithubre.ui.MainActivity.Companion.showToast
import com.m.ginwa.dicoding.mygithubre.ui.detail.DetailViewModel
import com.m.ginwa.dicoding.mygithubre.utils.RecyclerViewClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bio.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_followers.*
import kotlinx.android.synthetic.main.recyclerview.*
import javax.inject.Inject

@AndroidEntryPoint
class FollowersFragment : Fragment(R.layout.fragment_followers) {
    @Inject
    lateinit var followersAdapter: FollowersAdapter
    private lateinit var viewManager: LinearLayoutManager
    private val detailViewModel: DetailViewModel by navGraphViewModels(R.id.nav_detail_graph) {
        defaultViewModelProviderFactory
    }
    private val activityViewModel: ActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        loadFollowers()
        setSwipeUpListener()
    }

    private fun setAdapter() {
        followersAdapter.updateDataSet(detailViewModel.dataSetFollowers)
        followersAdapter.setOnRecyclerViewClick(object : RecyclerViewClickListener {
            override fun onClick(bundle: Bundle) {
                findNavController().apply {
                    if (currentDestination?.id == R.id.detailUserFragment) {
                        activityViewModel.fabIconListener.value = null
                        detailViewModel.dataSetFollowers = null
                        navigate(R.id.action_detailUserFragment_self, bundle)
                    }
                }
            }
        })
        viewManager = LinearLayoutManager(requireContext())
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = followersAdapter
            layoutManager = viewManager
            isNestedScrollingEnabled = false
        }
    }

    private fun setSwipeUpListener() {
        swipeRefreshLayout.setOnRefreshListener { detailViewModel.swipeUpListener.value = true }
        detailViewModel.swipeUpListener.observe(viewLifecycleOwner, Observer {
            if (it != null && it) {
                loadFollowers()
            }
        })
    }

    private fun loadFollowers() {
        detailViewModel.apply {
            isLoadFollowersComplete = false
            getFollowers().observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Result.Success -> {
                        result.data?.let { dataSet ->
                            followersAdapter.updateDataSet(dataSet)
                        }
                    }
                    is Result.Error -> {
                        showToast(requireContext(), result.toString())
                        errorOrComplete()
                    }
                    Result.Loading -> activityViewModel.progressBarLive.value = true
                    Result.Complete -> {
                        errorOrComplete()
                    }
                }
            })
        }
    }

    private fun errorOrComplete() {
        detailViewModel.isLoadFollowersComplete = true
        swipeRefreshLayout.isRefreshing = false
        if (detailViewModel.isHideIndicator()) activityViewModel.progressBarLive.value = false
        if (followersAdapter.dataSet.isEmpty()) textNoticeContainer.visibility = VISIBLE
        else textNoticeContainer.visibility = GONE
    }

    companion object {
        fun newInstance(): FollowersFragment =
            FollowersFragment()
    }


}