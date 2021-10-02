package com.alexgrig.education.problemsolver.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerDialogFragment : DialogFragment() {

    interface Callbacks {
        fun onDateSelected(date: Date)
    }

    //создаём реализацию интерфейса DatePickerDialog.OnDateSetListener (анонимный класс,
    //реализующий данный интерфейс) чтобы передать её в конструктор
    private val listener = DatePickerDialog.OnDateSetListener {
            _: DatePicker, year: Int, month: Int, day: Int ->

        val resultDate: Date = GregorianCalendar(year, month, day).time

        targetFragment?.let { (it as Callbacks).onDateSelected(resultDate) }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date

        return DatePickerDialog(
            requireContext(),
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    companion object {

        private const val ARG_DATE = "date"

        fun newInstance(date: Date) : DatePickerDialogFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickerDialogFragment().apply {
                arguments = args
            }
        }
    }

}