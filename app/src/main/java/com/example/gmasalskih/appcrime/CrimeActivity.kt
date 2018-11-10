package com.example.gmasalskih.appcrime

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import java.util.*

class CrimeActivity : SingleFragmentActivity() {
    companion object {
        private const val EXTRA_CRIME_ID = "crime_id"
        fun newIntent(packageContext: Context, crimeId: UUID): Intent {
            return Intent(packageContext, CrimeActivity::class.java)
                .apply { putExtra(EXTRA_CRIME_ID, crimeId) }

        }
    }

    override fun createFragment(): Fragment {
        return CrimeFragment.newInstance(intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID)
    }
}
