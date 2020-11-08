package com.m.ginwa.dicoding.mygithubre.ui.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.ui.ActivityViewModel
import com.m.ginwa.dicoding.mygithubre.utils.RecyclerDeleteListener
import com.m.ginwa.dicoding.mygithubre.utils.RecyclerViewClickListener
import com.m.ginwa.dicoding.mygithubre.utils.SwipeToDelete
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment(R.layout.fragment_search_user) {

    @Inject
    lateinit var favoriteAdapter: FavoriteAdapter

    private var snackBar: Snackbar? = null
    private lateinit var viewManager: LinearLayoutManager
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val activityViewModel: ActivityViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch { setAdapter() }
    }

    private suspend fun setAdapter() {
        favoriteAdapter.updateDataSet(
            favoriteViewModel.dataSet ?: favoriteViewModel.getUserFavoriteAsync().await()
        )
        favoriteViewModel.dataSet = favoriteViewModel.getUserFavoriteAsync().await()
        val itemTouchHelper = ItemTouchHelper(SwipeToDelete(favoriteAdapter))
        favoriteAdapter.setOnRecyclerViewClick(object : RecyclerViewClickListener {
            override fun onClick(bundle: Bundle) {
                findNavController().apply {
                    if (currentDestination?.id == R.id.favoriteFragment) {
                        navigate(R.id.to_nav_detail_graph, bundle)
                    }
                }
            }
        })
        favoriteAdapter.setOnRecyclerViewDeleteListener(object : RecyclerDeleteListener {
            override fun onDelete(
                adapterPosition: Int?,
                user: User?
            ) {
                updateUserIsFavorite(isFavorite = false, isScrollToBottom = false)
                showUndoSnackBar(user)
            }
        })
        viewManager = LinearLayoutManager(requireContext())
        recyclerView.apply {
            itemTouchHelper.attachToRecyclerView(this)
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = favoriteAdapter
            isNestedScrollingEnabled = false
        }
        swipeRefreshLayout.isEnabled = false
    }

    private fun showUndoSnackBar(user: User?) {
        val message =
            "${getString(R.string.delete)} ${user?.login} ${getString(R.string.from_list)}"
        snackBar = Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_LONG
        )
        snackBar?.setAction(getString(R.string.undo)) {
            undo()
        }
        snackBar?.show()
    }

    override fun onDestroy() {
        snackBar?.dismiss()
        super.onDestroy()

    }

    private fun undo() {
        val user = favoriteAdapter.recentDeleteData
        user?.isFavorite = true
        favoriteAdapter.updateData(user)
        updateUserIsFavorite(
            isFavorite = true,
            isScrollToBottom = true
        )
    }

    private fun updateUserIsFavorite(isFavorite: Boolean, isScrollToBottom: Boolean) {
        // update user
        val user = favoriteAdapter.recentDeleteData
        user?.isFavorite = isFavorite
        activityViewModel.insertUser(user)
        if (isScrollToBottom) viewManager.smoothScrollToPosition(
            recyclerView,
            RecyclerView.State(),
            favoriteAdapter.dataSet.lastIndex
        )
    }


}