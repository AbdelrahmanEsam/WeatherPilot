package com.example.weatherpilot.presentation.datePicker

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.weatherpilot.R
import java.util.Calendar

class DatePickerFragment : DialogFragment(), OnDateSetListener {
    private lateinit  var navController: NavController
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        navController = NavHostFragment.findNavController(this)
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val dialog = DatePickerDialog(requireContext(), R.style.DatePickerTheme, this, year, month, day)
        dialog.datePicker.minDate = calendar.time.time
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        dialog.datePicker.maxDate = calendar.time.time
        return dialog
    }

    override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
        navController.previousBackStackEntry!!.savedStateHandle[getString(R.string.date)] = day
    }
}