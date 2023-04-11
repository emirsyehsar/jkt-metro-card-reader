package com.example.kmtreader.model

import com.example.kmtreader.model.enums.Station
import com.example.kmtreader.model.enums.Transaction

data class History(
    val balanceChange: Int,
    val credit: Boolean,
    val journeyDate: String,
    val station: Station?,
    val timestamp: Long,
    val transaction: Transaction
)
