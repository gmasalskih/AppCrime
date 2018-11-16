package com.example.gmasalskih.appcrime.database

class CrimeDbSchema {
    public class CrimeTable {
        companion object {
            public val NAME = "crimes"
        }

        class Cols {
            companion object {
                val UUID = "uuid"
                val TITLE = "title"
                val DATE = "date"
                val SOLVED = "solved"
            }
        }
    }
}