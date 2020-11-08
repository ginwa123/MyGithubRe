package com.m.ginwa.dicoding.mygithubre.ui.detail

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.ui.ActivityViewModel
import com.m.ginwa.dicoding.mygithubre.ui.MainActivity.Companion.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bio.*

@AndroidEntryPoint
class BioFragment : Fragment(R.layout.fragment_bio) {

    private val detailViewModel: DetailViewModel by navGraphViewModels(R.id.nav_detail_graph) {
        defaultViewModelProviderFactory
    }
    private val activityViewModel: ActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserProfile(detailViewModel.dataUser)
        loadUser()
        setSwipeUpListener()
    }

    private fun setSwipeUpListener() {
        swipeRefreshLayout.setOnRefreshListener { detailViewModel.swipeUpListener.value = true }
        detailViewModel.swipeUpListener.observe(viewLifecycleOwner, Observer {
            if (it != null && it) {
                loadUser()
            }
        })
    }

    private fun loadUser() {
        detailViewModel.apply {
            isLoadUserComplete = false
            getUser().observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Result.Success -> {
                        setUserProfile(result.data)
                        activityViewModel.imageToolbarListener.value = result.data?.avatarUrl
                    }
                    is Result.Error -> {
                        showToast(requireContext(), result.toString())
                        errorOrComplete()
                    }
                    Result.Loading -> activityViewModel.progressBarLive.value = true
                    Result.Complete -> {
                        errorOrComplete()
                        activityViewModel.fabIconListener.value = dataUser
                    }
                }
            })

        }
    }

    private fun errorOrComplete() {
        detailViewModel.isLoadUserComplete = true
        if (detailViewModel.isHideIndicator()) activityViewModel.progressBarLive.value = false
        swipeRefreshLayout.isRefreshing = false
    }

    private fun setUserProfile(user: User?) {
        user?.apply {
            textName.text = name
            textCompany.text = company
            textLocation.text = location
            textRepository.text =
                String.format("${getString(R.string.repositories)} : $publicRepos")
            textFollower.text = String.format("${getString(R.string.followers)} : $followers")
            textFollowing.text = String.format("${getString(R.string.following)} : $following")
            if (!textName.text.isNullOrEmpty()) textNameContainer.visibility = VISIBLE
            else textNameContainer.visibility = GONE

            if (!textCompany.text.isNullOrEmpty()) textCompanyContainer.visibility = VISIBLE
            else textCompanyContainer.visibility = GONE

            if (!textLocation.text.isNullOrEmpty()) textLocationContainer.visibility = VISIBLE
            else textLocationContainer.visibility = GONE

            if (!textRepository.text.isNullOrEmpty()) textRepositoryContainer.visibility = VISIBLE
            else textRepositoryContainer.visibility = GONE

            if (!textFollower.text.isNullOrEmpty()) textFollowersContainer.visibility = VISIBLE
            else textFollowersContainer.visibility = GONE

            if (!textFollowing.text.isNullOrEmpty()) textFollowingContainer.visibility = VISIBLE
            else textFollowingContainer.visibility = GONE


        }
    }

    companion object {
        fun newInstance() = BioFragment()
    }


}