package com.example.gmasalskih.appcrime.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.gmasalskih.appcrime.database.CrimeDbSchema.*

class CrimeBaseHelper(val context : Context): SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    companion object {
        const val VERSION = 1
        const val DATABASE_NAME = "crimeBase.db"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            create table ${CrimeTable.NAME}
            (
            _id integer primary key autoincrement,
            ${CrimeTable.Cols.UUID},
            ${CrimeTable.Cols.TITLE},
            ${CrimeTable.Cols.DATE},
            ${CrimeTable.Cols.SOLVED}
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented")
    }


}