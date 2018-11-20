package com.example.gmasalskih.appcrime

import java.util.*

data class Crime(
    val mId: UUID = UUID.randomUUID(),
    var mTitle: String = "",
    var mDate: Date = Date(),
    var mSolved: Boolean = false,
    var mSuspect: String?
)