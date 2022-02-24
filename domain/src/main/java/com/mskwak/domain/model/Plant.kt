package com.mskwak.domain.model

import android.net.Uri
import java.time.LocalDate
import java.time.LocalDateTime

interface Plant {
    val id: Int
    val name: String
    val createdDate: LocalDateTime
    val waterPeriod: Int
    val lastWateringDate: LocalDate
    val wateringAlarm: Alarm
    val pictureUri: Uri?
    val memo: String?
}