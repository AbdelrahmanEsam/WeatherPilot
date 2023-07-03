package com.example.weatherpilot.presentation.timepicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.weatherpilot.R
import com.example.weatherpilot.databinding.FragmentTimePickerBinding
import java.util.Calendar


class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit  var navController: NavController

    private lateinit var binding: FragmentTimePickerBinding

    private val calender by lazy { Calendar.getInstance() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_picker, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        navController = NavHostFragment.findNavController(this)
        return TimePickerDialog(
            requireContext(),
            this,
            calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE),
            false
        )
    }


    override fun onTimeSet(timePicker: TimePicker, hour: Int, minute: Int) {
        navController.previousBackStackEntry!!.savedStateHandle[getString(R.string.time)] = "$hour $minute"
    }
}