package com.mskwak.domain.model

import android.net.Uri
import java.time.LocalDate

interface Plant {
    val id: Int
    val name: String
    val createdDate: LocalDate
    val waterPeriod: Int
    val lastWateringDate: LocalDate
    val wateringAlarm: Alarm
    val pictureUri: Uri?
    val memo: String?
}