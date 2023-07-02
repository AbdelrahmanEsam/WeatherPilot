package com.example.weatherpilot.presentation.favourites

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentFavouritesBinding
import com.example.weatherpilot.presentation.main.DaysRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private lateinit var binding: FragmentFavouritesBinding

    private lateinit var navController: NavController

    private val favouritesAdapter by lazy {
        FavouritesRecyclerAdapter(onClickAction = { onFavouriteItemClickAction(it) },
            { onFavouriteItemLongClickAction(it) })
    }


    private val viewModel: FavouriteViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setFavouritesRecyclerView()
        favouritesStateObserver()
        swipeRecyclerItemListener()
        viewModel.onEvent(FavouritesIntent.FetchFavouritesFromDatabase)

        binding.addNewFavouriteButton.setOnClickListener {
            navController.navigate(FavouritesFragmentDirections.actionFavouritesFragmentToMapFragment(getString(R.string.from_favourite_fragment)))
        }
    }

    private fun setFavouritesRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.favouritesRecycler.layoutManager = linearLayoutManager
        binding.favouritesRecycler.adapter = favouritesAdapter
    }


    private fun swipeRecyclerItemListener()
    {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.onEvent(FavouritesIntent.DeleteItem(viewModel.favouriteState.value.favourites!![viewHolder.adapterPosition]))
            }

        }).attachToRecyclerView(binding.favouritesRecycler)
    }


    private fun favouritesStateObserver() {
        lifecycleScope.launch {
            viewModel.favouriteState.collect {

                if (it.favourites?.isNotEmpty() == true) {
                    binding.favouritesRecycler.visibility = View.VISIBLE
                    binding.noThingToShowImageView.visibility = View.GONE
                    binding.noFavouritesTextView.visibility = View.GONE
                    favouritesAdapter.submitList(it.favourites)
                }else{
                    binding.favouritesRecycler.visibility = View.GONE
                    binding.noThingToShowImageView.visibility = View.VISIBLE
                    binding.noFavouritesTextView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onFavouriteItemClickAction(position: Int) {
       val favouriteItem =  viewModel.favouriteState.value.favourites?.get(position)
        Log.d("item",favouriteItem.toString())
           navController.navigate(FavouritesFragmentDirections.actionFavouritesFragmentToHomeFragment(favouriteItem))
    }


    private fun onFavouriteItemLongClickAction(position: Int) {

    }


}