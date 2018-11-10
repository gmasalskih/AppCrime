package com.example.gmasalskih.appcrime

import android.content.Context
import java.util.*

class CrimeLab private constructor(context: Context) {
    companion object {
        private var sCrimeLab: CrimeLab? = null
        fun get(context: Context): CrimeLab {
            if (sCrimeLab == null) {
                sCrimeLab = CrimeLab(context)
            }
            return sCrimeLab as CrimeLab
        }
    }

    private var mCrimes = List(100) {
        val crime = Crime()
        crime.mTitle = "Crime #$it"
        crime.mSolved = it % 2 == 0
        crime
    }

    fun getCrimes() = mCrimes

    fun getCrime(id: UUID) = mCrimes.single { it.mId == id }

}