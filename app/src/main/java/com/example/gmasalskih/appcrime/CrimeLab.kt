package com.example.gmasalskih.appcrime

import android.content.ContentValues
import android.content.Context
import com.example.gmasalskih.appcrime.database.CrimeBaseHelper
import com.example.gmasalskih.appcrime.database.CrimeCursorWrapper

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

    private var mContext: Context = context.applicationContext
    private val mDateBase = CrimeBaseHelper(mContext).writableDatabase

    fun getCrimes(): List<Crime> {
        val crimes = mutableListOf<Crime>()
        val cursor = queryCrimes(null, null)
        cursor.use {
            it.moveToFirst()
            while (!it.isAfterLast) {
                crimes.add(it.getCrime())
                it.moveToNext()
            }
        }
        return crimes.toList()
    }

    fun getCrime(id: UUID): Crime {
        val cursor = queryCrimes(
            "${CrimeTable.Cols.UUID} = ?",
            Array(1) { "$id" }
        )
        return cursor.use {
            if (it.count == 0) return Crime()
            it.moveToFirst()
            it.getCrime()
        }
    }

    fun updateCrime(crime: Crime) {
        mDateBase.update(CrimeTable.NAME,
            getContentValues(crime),
            "${CrimeTable.Cols.UUID} = ?",
            Array(1) { "${crime.mId}" }
        )
    }

    fun queryCrimes(whereClause: String?, whereArgs: Array<String>?): CrimeCursorWrapper {
        val cursor = mDateBase.query(
            CrimeTable.NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null
        )
        return CrimeCursorWrapper(cursor)
    }

    fun addCrime(crime: Crime) {
        mDateBase.insert(CrimeTable.NAME, null, getContentValues(crime))
    }

    fun delCrimeById(id: UUID) {
        mDateBase.delete(
            CrimeTable.NAME,
            "${CrimeTable.Cols.UUID} = ?",
            Array(1) { "$id" }
        )
    }

}