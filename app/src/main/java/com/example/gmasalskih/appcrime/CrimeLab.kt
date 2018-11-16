package com.example.gmasalskih.appcrime

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.gmasalskih.appcrime.database.CrimeBaseHelper

import com.example.gmasalskih.appcrime.database.CrimeDbSchema.*
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

        private fun getContentValues(crime: Crime): ContentValues {
            return ContentValues().apply {
                put(CrimeTable.Cols.UUID, crime.mId.toString())
                put(CrimeTable.Cols.DATE, crime.mDate.time)
                put(CrimeTable.Cols.SOLVED, if (crime.mSolved) 1 else 0)
                put(CrimeTable.Cols.TITLE, crime.mTitle)
            }
        }
    }

    //    private var mCrimes = mutableListOf<Crime>()
    private var mContext: Context = context.applicationContext
    private val mDatebase = CrimeBaseHelper(mContext).writableDatabase


    fun getCrimes(): List<Crime> {

        return mutableListOf()
    }

    fun getCrime(id: UUID): Crime {


        return Crime()
    }

    fun updateCrime(crime: Crime) {
        mDatebase.update(CrimeTable.NAME,
            getContentValues(crime),
            "${CrimeTable.Cols.UUID} = ?",
            Array(1) { "${crime.mId}" }
        )
    }

    fun queryCrimes(whereClause: String, whereArgs: Array<String>): Cursor {
        return mDatebase.query(
            CrimeTable.NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null
        )
    }

    fun addCrime(crime: Crime) {
        mDatebase.insert(CrimeTable.NAME, null, getContentValues(crime))
    }

}