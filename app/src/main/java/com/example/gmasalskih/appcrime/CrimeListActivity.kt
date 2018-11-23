package com.example.gmasalskih.appcrime

import android.support.v4.app.Fragment

class CrimeListActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return CrimeListFragment()
    }

    override fun getLayoutResId():Int{
        return R.layout.activity_masterdetail
    }
}