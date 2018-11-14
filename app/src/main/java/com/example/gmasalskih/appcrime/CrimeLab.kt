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

    private var mCrimes = mutableListOf<Crime>()

    fun getCrimes() = mCrimes

    fun getCrime(id: UUID) = mCrimes.single { it.mId == id }

    fun addCrime(crime: Crime) {
        mCrimes.add(crime)
    }

}