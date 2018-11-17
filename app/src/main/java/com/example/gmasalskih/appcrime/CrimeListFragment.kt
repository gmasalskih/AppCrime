package com.example.gmasalskih.appcrime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.example.gmasalskih.appcrime.Utils.toFormattedString
import java.util.*

class CrimeListFragment : Fragment() {

    private lateinit var mCrimeRecyclerView: RecyclerView
    private var mAdapter: CrimeAdapter? = null
    private var mSubtitleVisible: Boolean = false

    companion object {
        const val SAVED_SUBTITLE_VISIBLE = "subtitle"
        const val DELETE = "delete"
        const val REQUEST_DATE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_crime_list, container, false)
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        mCrimeRecyclerView.layoutManager = LinearLayoutManager(activity)
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE)
        }

        updateUI()
        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_crime_list, menu)
        val subtitleItem = menu?.findItem(R.id.show_subtitle)
        if (mSubtitleVisible) {
            subtitleItem?.setTitle(R.string.hide_subtitle)
        } else {
            subtitleItem?.setTitle(R.string.show_subtitle)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                CrimeLab.get(activity as Context).addCrime(crime)
                val intent = CrimePagerActivity.newIntent(activity as Context, crime.mId)
                startActivity(intent)
                true
            }
            R.id.show_subtitle -> {
                mSubtitleVisible = !mSubtitleVisible
                activity?.invalidateOptionsMenu()
                updateSubtitle()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun updateSubtitle() {
        val crimeLab = CrimeLab.get(activity as Context)
        val crimeCount = crimeLab.getCrimes().size
        var subtitle: String? = getString(R.string.subtitle_format, crimeCount)
        if (!mSubtitleVisible) subtitle = null
        (activity as AppCompatActivity).supportActionBar?.subtitle = subtitle
    }

    private fun updateUI() {
        val crimeLab: CrimeLab = CrimeLab.get(activity as Context)
        val crimes: List<Crime> = crimeLab.getCrimes()
        if (mAdapter == null) {
            mAdapter = CrimeAdapter(crimes)
            mCrimeRecyclerView.adapter = mAdapter
        } else {
            mAdapter?.setCrimes(crimes)
            mAdapter?.notifyDataSetChanged()
        }
        updateSubtitle()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_DATE) {
            val id = data?.getSerializableExtra(DeleteCrimeFragment.RESULT) as UUID
            CrimeLab.get(activity as Context).delCrimeById(id)
            updateUI()
        }
    }

    private inner class CrimeHolder(val inflater: LayoutInflater, val parent: ViewGroup, val viewType: Int) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_crime, parent, false)) {

        private val mTitleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val mDateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val mSolvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)
        private lateinit var mCrime: Crime

        fun bind(crime: Crime) {
            mCrime = crime
            mTitleTextView.text = mCrime.mTitle
            if (viewType == 0) mTitleTextView.setTypeface(null, Typeface.BOLD)
            mDateTextView.text = mCrime.mDate.toFormattedString()
            mSolvedImageView.visibility = if (crime.mSolved) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                startActivity(CrimePagerActivity.newIntent(activity as Context, mCrime.mId))
            }
            itemView.setOnLongClickListener {
                //                updateUI()
                val fm = fragmentManager
                val deleteDialog = DeleteCrimeFragment.newInstance(mCrime.mTitle, mCrime.mId)
                deleteDialog.setTargetFragment(this@CrimeListFragment, REQUEST_DATE)
                deleteDialog.show(fm, DELETE)

                true
            }
        }
    }

    private inner class CrimeAdapter(var mCrimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val layoutInflater: LayoutInflater = LayoutInflater.from(activity)
            Log.d("Q", viewType.toString())
            return CrimeHolder(layoutInflater, parent, viewType)
        }

        override fun getItemViewType(position: Int): Int {
            return if (mCrimes[position].mSolved) 1
            else 0
        }

        override fun getItemCount(): Int {
            return mCrimes.size
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime: Crime = mCrimes[position]
            holder.bind(crime)
        }

        public fun setCrimes(crimes: List<Crime>) {
            mCrimes = crimes
        }
    }
}