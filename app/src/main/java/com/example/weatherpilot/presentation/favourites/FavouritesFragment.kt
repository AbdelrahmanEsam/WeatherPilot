package com.example.weatherpilot.presentation.favourites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.NavGraphDirections
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentFavouritesBinding
import com.example.weatherpilot.util.connectivity.ConnectivityObserver
import com.example.weatherpilot.util.usescases.swipeRecyclerItemListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class FavouritesFragment(private val connectivityObserver: ConnectivityObserver) : Fragment() {

    private lateinit var binding: FragmentFavouritesBinding

    private lateinit var navController: NavController

    private val favouritesAdapter by lazy {
        FavouritesRecyclerAdapter {
            onFavouriteItemClickAction(it)
        }
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
        viewModel.onEvent(FavouritesIntent.FetchFavouritesFromDatabase)

        binding.addNewFavouriteButton.setOnClickListener {
            navController.navigate(
                NavGraphDirections.actionToMapFragment(
                    getString(R.string.from_favourite_fragment)
                )
            )
        }
    }

    private fun setFavouritesRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.favouritesRecycler.swipeRecyclerItemListener { viewHolder ->
            viewModel.onEvent(FavouritesIntent.DeleteItem(viewModel.favouriteState.value.favourites!![viewHolder.adapterPosition]))
        }
        binding.favouritesRecycler.layoutManager = linearLayoutManager
        binding.favouritesRecycler.adapter = favouritesAdapter
    }


    private fun favouritesStateObserver() {
        lifecycleScope.launch {
            viewModel.favouriteState.collect {

                if (it.favourites?.isNotEmpty() == true) {
                    binding.favouritesRecycler.visibility = View.VISIBLE
                    binding.noThingToShowImageView.visibility = View.GONE
                    binding.noFavouritesTextView.visibility = View.GONE
                    favouritesAdapter.submitList(it.favourites)
                } else {
                    binding.favouritesRecycler.visibility = View.GONE
                    binding.noThingToShowImageView.visibility = View.VISIBLE
                    binding.noFavouritesTextView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onFavouriteItemClickAction(position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            connectivityObserver.observe().collectLatest {
                delay(500)

                withContext(Dispatchers.Main) {
                    if (it == ConnectivityObserver.Status.Available) {


                        val favouriteItemLocation =
                            viewModel.favouriteState.value.favourites?.get(position)
                        navController.navigate(
                            NavGraphDirections.actionToHomeFragment(
                                favouriteItemLocation
                            )
                        )
                    } else {

                        Snackbar.make(
                            binding.root,
                            getString(R.string.please_connect_to_internet),
                            Snackbar.LENGTH_LONG
                        )
                            .setActionTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                            .setBackgroundTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.baby_blue
                                )
                            )
                            .show()
                    }
                }
            }
        }
    }


}