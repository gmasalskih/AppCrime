package com.example.gmasalskih.appcrime

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.gmasalskih.appcrime.Utils.toFormattedString

class CrimeListFragment : Fragment() {

    private lateinit var mCrimeRecyclerView: RecyclerView
    private var mAdapter: CrimeAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_crime_list, container, false)
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        mCrimeRecyclerView.layoutManager = LinearLayoutManager(activity)
        updateUI()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val crimeLab: CrimeLab = CrimeLab.get(activity as Context)
        val crimes: List<Crime> = crimeLab.getCrimes()
        if (mAdapter == null) {
            mAdapter = CrimeAdapter(crimes)
            mCrimeRecyclerView.adapter = mAdapter
        } else {
            mAdapter?.notifyDataSetChanged()
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
                val intent = CrimeActivity.newIntent(activity as Context, mCrime.mId)
                startActivity(intent)
            }
        }
    }

    private inner class CrimeAdapter(val mCrimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {

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
    }
}