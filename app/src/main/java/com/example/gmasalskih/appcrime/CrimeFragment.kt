package com.example.gmasalskih.appcrime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.gmasalskih.appcrime.Utils.toFormattedString
import java.io.File
import java.util.*

class CrimeFragment : Fragment() {

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"
        private const val REQUEST_DATE = 0
        private const val REQUEST_CONTACT = 1
        private const val REQUEST_PHOTO = 2
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
    private lateinit var mPhotoButton: ImageButton
    private lateinit var mPhotoView: ImageView
    private lateinit var mPhotoFile: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        mCrime = CrimeLab.get(activity as Context).getCrime(crimeId)
        mPhotoFile = CrimeLab.get(activity as Context).getPhotoFile(mCrime)
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
        val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        mSuspectButton.setOnClickListener {
            startActivityForResult(pickContact, REQUEST_CONTACT)
        }
        if (mCrime.mSuspect != null) mSuspectButton.text = mCrime.mSuspect

        val pm = activity?.packageManager
        if (pm?.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null)
            mSuspectButton.isEnabled = false

        mPhotoButton = v.findViewById(R.id.crime_camera)
        mPhotoView = v.findViewById(R.id.crime_photo)

        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(pm) != null
        mPhotoButton.isEnabled = canTakePhoto
        mPhotoButton.setOnClickListener {
            val uri = FileProvider.getUriForFile(
                activity as Context,
                "com.example.gmasalskih.appcrime.fileprovider",
                mPhotoFile
            )
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            val cameraActivities = pm?.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            cameraActivities?.forEach { resolveInfo ->
                activity?.grantUriPermission(
                    resolveInfo.activityInfo.packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            startActivityForResult(captureImage, REQUEST_PHOTO)
        }

        return v
    }

    override fun onPause() {
        super.onPause()
        CrimeLab.get(activity as Context).updateCrime(mCrime)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            REQUEST_DATE -> {
                val date = data?.getSerializableExtra(DatePickerFragment.EXTRA_DATE)
                if (date != null) {
                    mCrime.mDate = date as Date
                    updateDate()
                }
            }
            REQUEST_CONTACT -> {
                if (data != null) {
                    val contactUri = data.data
                    val queryFields = Array(1) {
                        ContactsContract.Contacts.DISPLAY_NAME
                    }
                    activity?.contentResolver?.query(contactUri, queryFields, null, null, null).use { cursor ->
                        if (cursor == null || cursor.count == 0) return
                        cursor.moveToFirst()
                        cursor.getString(0).let { str ->
                            mCrime.mSuspect = str
                            mSuspectButton.text = str
                        }
                    }
                }
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