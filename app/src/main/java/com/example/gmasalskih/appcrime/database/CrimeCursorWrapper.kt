package com.example.gmasalskih.appcrime.database

import android.database.Cursor
import android.database.CursorWrapper
import com.example.gmasalskih.appcrime.Crime
import com.example.gmasalskih.appcrime.database.CrimeDbSchema.*
import java.util.*

class CrimeCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    fun getCrime(): Crime {
        val uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID))
        val title = getString(getColumnIndex(CrimeTable.Cols.TITLE))
        val date = getLong(getColumnIndex(CrimeTable.Cols.DATE))
        val isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED))

        return Crime(
            UUID.fromString(uuidString),
            title,
            Date(date),
            isSolved != 0
        )
    }

}