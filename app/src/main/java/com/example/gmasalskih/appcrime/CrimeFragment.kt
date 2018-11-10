package com.example.gmasalskih.appcrime

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText

class CrimeFragment : Fragment() {

    private lateinit var mCrime: Crime
    private lateinit var mTitleField: EditText
    private lateinit var mDateButton: Button
    private lateinit var mSolvedCheckBox: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCrime = Crime()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_crime, container, false)

        mTitleField = v.findViewById(R.id.crime_title) as EditText
        mTitleField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                TODO("not implemented")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("not implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mCrime.mTitle = s.toString()
            }
        })

        mDateButton = v.findViewById(R.id.crime_date)
        mDateButton.text = mCrime.mDate.toString()
        mDateButton.isEnabled = false

        mSolvedCheckBox = v.findViewById(R.id.crime_solved)
        mSolvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            mCrime.mSolved = isChecked
        }




        return v
    }
}