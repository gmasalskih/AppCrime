package com.example.gmasalskih.appcrime

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import kotlinx.android.synthetic.main.activity_crime_pager.*
import java.util.*

class CrimePagerActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_CRIME_ID = "crime_id"
        fun newIntent(packageContext: Context, crimeId: UUID): Intent {
            return Intent(packageContext, CrimePagerActivity::class.java).apply {
                putExtra(EXTRA_CRIME_ID, crimeId)
            }
        }
    }

    private lateinit var mViewPager: ViewPager
    private lateinit var mCrimes: List<Crime>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)

        mViewPager = crime_view_pager
        mCrimes = CrimeLab.get(this as Context).getCrimes()

        val fm: FragmentManager = supportFragmentManager
        mViewPager.adapter = object : FragmentStatePagerAdapter(fm) {
            override fun getItem(position: Int): Fragment {
                return CrimeFragment.newInstance(mCrimes[position].mId)
            }

            override fun getCount(): Int {
                return mCrimes.size
            }
        }

        val crimeId: UUID = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        mViewPager.currentItem = mCrimes.indexOfFirst {
            it.mId == crimeId
        }
    }
}
