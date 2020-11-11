package com.m.ginwa.dicoding.mygithubre.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.ui.ActivityViewModel
import com.m.ginwa.dicoding.mygithubre.ui.MainActivity.Companion.showToast
import com.m.ginwa.dicoding.mygithubre.utils.RecyclerViewClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search_user.*
import kotlinx.android.synthetic.main.recyclerview.*
import javax.inject.Inject

@AndroidEntryPoint
class SearchUserFragment : Fragment(R.layout.fragment_search_user) {

    @Inject
    lateinit var searchUserAdapter: SearchUserAdapter

    private lateinit var searchView: SearchView
    private lateinit var viewManager: LinearLayoutManager
    private val searchUserViewModel: SearchUserViewModel by viewModels()
    private val activityViewModel: ActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
    }

    private fun setAdapter() {
        searchUserAdapter.updateDataSet(searchUserViewModel.dataSet)
        searchUserAdapter.setOnRecyclerViewClick(object : RecyclerViewClickListener {
            override fun onClick(bundle: Bundle) {
                findNavController().apply {
                    if (currentDestination?.id == R.id.searchUserFragment) {
                        navigate(R.id.to_nav_detail_graph, bundle)
                    }
                }
            }
        })
        viewManager = LinearLayoutManager(requireContext())
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = searchUserAdapter
            isNestedScrollingEnabled = false
        }
        swipeRefreshLayout.isEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        searchView = SearchView(requireContext())
        menu.findItem(R.id.action_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    loadUsers(query)
                    searchView.clearFocus()
                    return false
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        if (activityViewModel.startOnce) {
            activityViewModel.startOnce = false
            searchView.setQuery("ginwa", true)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.apply {
            if (itemId == R.id.action_to_favoriteFragment) {
                activityViewModel.progressBarLive.value = null
            } else if (itemId == R.id.action_to_settingsFragment) {
                activityViewModel.progressBarLive.value = null
            }
        }
        return item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(
            item
        )
    }


    private fun loadUsers(username: String) {
        if (username.isNotEmpty()) {
            searchUserViewModel.loadUsers(username).observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Result.Success -> {
                        result.data?.items?.let { dataSet ->
                            searchUserViewModel.dataSet = dataSet
                            searchUserAdapter.updateDataSet(dataSet)
                            activityViewModel.progressBarLive.value = false
                            if (searchUserAdapter.dataSet.isEmpty()) {
                                val notice =
                                    "${getString(R.string.could_not_find_any_user_matching)} '${searchView.query}'"
                                textNotice.text = String.format(notice)
                                textNoticeContainer.visibility = VISIBLE
                                return@Observer
                            }
                            viewManager.smoothScrollToPosition(
                                recyclerView,
                                RecyclerView.State(),
                                0
                            )
                            textNoticeContainer.visibility = GONE
                        }

                    }
                    is Result.Error -> {
                        activityViewModel.progressBarLive.value = false
                        showToast(requireContext(), result.toString())
                    }
                    Result.Loading -> activityViewModel.progressBarLive.value = true
                    Result.Complete -> {

                    }
                }
            })
        }
    }
}