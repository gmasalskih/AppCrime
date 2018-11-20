package com.example.gmasalskih.appcrime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.example.gmasalskih.appcrime.Utils.toFormattedString
import java.util.*

class CrimeFragment : Fragment() {

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"
        private const val REQUEST_DATE = 0
        private const val REQUEST_CONTACT = 1
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args: Bundle = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    private lateinit var mCrime: Crime
    private lateinit var mTitleField: EditText
    private lateinit var mDateButton: Button
    private lateinit var mSolvedCheckBox: CheckBox
    private lateinit var mReportButton: Button
    private lateinit var mSuspectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        mCrime = CrimeLab.get(activity as Context).getCrime(crimeId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_crime, container, false)

        mTitleField = v.findViewById(R.id.crime_title) as EditText
        mTitleField.setText(mCrime.mTitle)
        mTitleField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString().trim()
                if (str == "") mCrime.mTitle = "No Title"
                else mCrime.mTitle = str
            }
        })

        mDateButton = v.findViewById(R.id.crime_date)
        updateDate()
        mDateButton.setOnClickListener {
            val dialog = DatePickerFragment.newInstance(mCrime.mDate)
            dialog.setTargetFragment(this, REQUEST_DATE)
            dialog.show(fragmentManager, DIALOG_DATE)
        }

        mSolvedCheckBox = v.findViewById(R.id.crime_solved)
        mSolvedCheckBox.isChecked = mCrime.mSolved
        mSolvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            mCrime.mSolved = isChecked
        }

        mReportButton = v.findViewById(R.id.crime_report)
        mReportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.send_report)))
        }

        mSuspectButton = v.findViewById(R.id.crime_suspect)
        mSuspectButton.setOnClickListener {
            startActivityForResult(
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),
                REQUEST_CONTACT
            )
        }
        if(mCrime.mSuspect != null) mSuspectButton.text = mCrime.mSuspect

        return v
    }

    override fun onPause() {
        super.onPause()
        CrimeLab.get(activity as Context).updateCrime(mCrime)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_DATE) {
            val date = data?.getSerializableExtra(DatePickerFragment.EXTRA_DATE)
            if (date != null) {
                mCrime.mDate = date as Date
                updateDate()
            }
        }
    }

    private fun updateDate() {
        mDateButton.text = mCrime.mDate.toFormattedString()
    }

    private fun getCrimeReport(): String {
        val solvedString = if (mCrime.mSolved) getString(R.string.crime_report_solved)
        else getString(R.string.crime_report_unsolved)

        val dateFormat = "EEE, MMM dd"
        val dateString = DateFormat.format(dateFormat, mCrime.mDate).toString()

        val suspect: String = if (mCrime.mSuspect == null) getString(R.string.crime_report_no_suspect)
        else getString(R.string.crime_report_suspect, mCrime.mSuspect)

        return getString(R.string.crime_report, mCrime.mTitle, dateString, solvedString, suspect)
    }
}