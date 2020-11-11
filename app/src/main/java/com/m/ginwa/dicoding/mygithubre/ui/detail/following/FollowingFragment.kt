package com.m.ginwa.dicoding.mygithubre.ui.detail.following

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.data.model.Following
import com.m.ginwa.dicoding.mygithubre.ui.ActivityViewModel
import com.m.ginwa.dicoding.mygithubre.ui.MainActivity.Companion.showToast
import com.m.ginwa.dicoding.mygithubre.ui.detail.DetailViewModel
import com.m.ginwa.dicoding.mygithubre.utils.RecyclerViewClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_following.*
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FollowingFragment : Fragment(R.layout.fragment_following) {

    private lateinit var followingAdapter: FollowingAdapter
    private lateinit var viewManager: LinearLayoutManager
    private val detailViewModel: DetailViewModel by navGraphViewModels(R.id.nav_detail_graph) {
        defaultViewModelProviderFactory
    }
    private val activityViewModel: ActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        loadFollowings()
        setSwipeUpListener()
    }

    private fun setAdapter() {
        followingAdapter = FollowingAdapter(diffCallback)
        followingAdapter.setOnRecyclerViewClick(object : RecyclerViewClickListener {
            override fun onClick(bundle: Bundle) {
                findNavController().apply {
                    if (currentDestination?.id == R.id.detailUserFragment) {
                        activityViewModel.fabIconListener.value = null
                        navigate(R.id.action_detailUserFragment_self, bundle)
                    }
                }
            }
        })
        viewManager = LinearLayoutManager(requireContext())
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = followingAdapter
            layoutManager = viewManager
            isNestedScrollingEnabled = false
        }
    }

    private fun setSwipeUpListener() {
        swipeRefreshLayout.setOnRefreshListener { detailViewModel.swipeUpListener.value = true }
        detailViewModel.swipeUpListener.observe(viewLifecycleOwner, {
            if (it != null && it) {
                loadFollowings()
            }
        })
    }

    private fun loadFollowings() {
        detailViewModel.apply {
            isLoadFollowingComplete = false
            getFollowings()
            followings.removeObservers(viewLifecycleOwner)
            followings.observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Result.Success -> if (result.data.isNotEmpty()) followingAdapter.submitList(
                        result.data
                    )
                    is Result.Error -> {
                        showToast(requireContext(), result.toString())
                        errorOrComplete()
                    }
                    Result.Loading -> activityViewModel.progressBarLive.value = true
                    Result.Complete -> errorOrComplete()
                }
            })
//            followingAdapter.registerAdapterDataObserver(object :
//                RecyclerView.AdapterDataObserver() {
//                override fun onChanged() {
//                    super.onChanged()
//                    if (followingAdapter.itemCount == 0) textNoticeContainer.visibility = VISIBLE
//                    else textNoticeContainer.visibility = GONE
//                }
//            })
        }
    }

    private fun errorOrComplete() {
        lifecycleScope.launch {
            delay(1000)
            detailViewModel.isLoadFollowingComplete = true
            swipeRefreshLayout.isRefreshing = false
            if (detailViewModel.isHideIndicator()) activityViewModel.progressBarLive.value = false
            if (followingAdapter.itemCount == 0) textNoticeContainer.visibility = VISIBLE
            else textNoticeContainer.visibility = GONE
        }
    }

    companion object {
        fun newInstance(): FollowingFragment = FollowingFragment()
    }

    private val diffCallback = object :
        DiffUtil.ItemCallback<Following>() {
        // Concert details may have changed if reloaded from the database,
        // but ID is fixed.
        override fun areItemsTheSame(
            oldFollowing: Following,
            newFollowing: Following
        ) = oldFollowing.login == newFollowing.login

        override fun areContentsTheSame(
            oldFollowing: Following,
            newFollowing: Following
        ) = oldFollowing == newFollowing
    }

}