package com.example.weatherpilot.presentation.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpilot.NavGraphDirections
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentNotificationsBinding
import com.example.weatherpilot.presentation.favourites.FavouritesIntent
import com.example.weatherpilot.util.swipeRecyclerItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding

    private lateinit var navController: NavController


    private val alertAdapter by lazy {
        AlertsRecyclerAdapter {
            onAlertItemClickAction(it)
        }
    }


    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setAlertsRecyclerView()
        notificationStateObserver()
        binding.addNewAlertButton.setOnClickListener {
            navController.navigate(NavGraphDirections.actionToMapFragment(getString(R.string.from_alerts_fragment)))
        }

    }


    private fun setAlertsRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.alertsRecycler.swipeRecyclerItemListener { viewHolder ->
            viewModel.onEvent(NotificationIntent.DeleteAlert(viewModel.alertsAndNotificationsState.value.alertsAndNotificationsList[viewHolder.adapterPosition]))
        }
        binding.alertsRecycler.layoutManager = linearLayoutManager
        binding.alertsRecycler.adapter = alertAdapter
    }


    private fun notificationStateObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.alertsAndNotificationsState.collectLatest { alertsAndNotificationsState ->
                    alertsAndNotificationsState.alertsAndNotificationsList.let { alertsList ->
                        if (alertsList.isNotEmpty()){
                            alertAdapter.submitList(alertsList)
                            binding.alertsRecycler.visibility = View.VISIBLE
                            binding.noThingToShowImageView.visibility = View.GONE
                            binding.noFavouritesTextView.visibility = View.GONE
                        }else{
                            binding.alertsRecycler.visibility = View.GONE
                            binding.noThingToShowImageView.visibility = View.VISIBLE
                            binding.noFavouritesTextView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }


    private fun onAlertItemClickAction(position: Int) {

    }


}