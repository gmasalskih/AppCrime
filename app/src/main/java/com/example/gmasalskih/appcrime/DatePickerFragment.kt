package com.example.gmasalskih.appcrime

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.widget.DatePicker
import java.util.*

class DatePickerFragment : DialogFragment() {

    companion object {
        private const val ARG_DATE = "date"
        const val EXTRA_DATE = "criminal.date"
        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }

    private lateinit var mDatePicker: DatePicker

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(ARG_DATE)
        val calendar = Calendar.getInstance().apply {
            if (date != null) time = date as Date
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val v = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_date, null)

        mDatePicker = v.findViewById(R.id.dialog_date_picker)
        mDatePicker.init(year, month, day, null)

        return AlertDialog.Builder(activity as Context)
            .setView(v)
            .setTitle(R.string.date_picker_title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val year = mDatePicker.year
                val month = mDatePicker.month
                val day = mDatePicker.dayOfMonth
                val date = GregorianCalendar(year,month,day).time
                sendResult(Activity.RESULT_OK, date)
            }
            .create()
    }

    private fun sendResult(resultCode: Int, date: Date) {
        val tf = targetFragment
        if (tf != null) {
            val intent = Intent().apply {
                putExtra(EXTRA_DATE, date)
            }
            tf.onActivityResult(targetRequestCode, resultCode, intent)
        }
    }
}