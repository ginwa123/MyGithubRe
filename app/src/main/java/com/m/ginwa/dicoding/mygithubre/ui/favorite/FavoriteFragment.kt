package com.m.ginwa.dicoding.mygithubre.ui.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.data.Result
import com.m.ginwa.dicoding.mygithubre.data.model.User
import com.m.ginwa.dicoding.mygithubre.ui.ActivityViewModel
import com.m.ginwa.dicoding.mygithubre.utils.RecyclerDeleteListener
import com.m.ginwa.dicoding.mygithubre.utils.RecyclerViewClickListener
import com.m.ginwa.dicoding.mygithubre.utils.SwipeToDelete
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview.*

@AndroidEntryPoint
class FavoriteFragment : Fragment(R.layout.fragment_search_user) {


    private lateinit var favoriteAdapter: FavoriteAdapter
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
        setAdapter()
    }

    private fun setAdapter() {
        favoriteAdapter = FavoriteAdapter(diffCallback)
        setData()
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
                updateUserIsFavorite(isFavorite = false)
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

    private fun setData() {
        favoriteViewModel.getUserFavorite()
        favoriteViewModel.dataSet.removeObservers(viewLifecycleOwner)
        favoriteViewModel.dataSet.observe(viewLifecycleOwner, {
            when (it) {
                is Result.Success -> favoriteAdapter.submitList(it.data)
                is Result.Error -> {

                }
                Result.Loading -> {

                }
                Result.Complete -> {
                }
            }
        })
    }

    private fun showUndoSnackBar(user: User?) {
        val message =
            "${getString(R.string.delete)} ${user?.login} ${getString(R.string.from_list)}"
        snackBar = Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_LONG
        )
        snackBar?.setAction(getString(R.string.undo)) { undo() }
        snackBar?.show()
    }

    override fun onDestroy() {
        snackBar?.dismiss()
        super.onDestroy()

    }

    private fun undo() {
        val user = favoriteAdapter.recentDeleteData
        user?.isFavorite = true
        updateUserIsFavorite(
            isFavorite = true
        )
    }

    private fun updateUserIsFavorite(isFavorite: Boolean) {
        // update user
        val user = favoriteAdapter.recentDeleteData
        user?.isFavorite = isFavorite
        activityViewModel.insertUser(user)
    }

    private val diffCallback = object :
        DiffUtil.ItemCallback<User>() {
        // Concert details may have changed if reloaded from the database,
        // but ID is fixed.
        override fun areItemsTheSame(
            oldUser: User,
            newUser: User
        ) = oldUser.login == newUser.login

        override fun areContentsTheSame(
            oldUser: User,
            newUser: User
        ) = oldUser == newUser
    }

}