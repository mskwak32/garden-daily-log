package com.mskwak.domain.model

import android.net.Uri
import java.util.*

interface PlantModel {
    val id: Int
    val name: String
    val createdDate: Date
    val waterPeriod: Int
    val lastWateringDate: Date
    val wateringAlarm: AlarmModel
    val pictureUri: Uri?
    val memo: String?
}