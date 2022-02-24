package com.mskwak.domain.model

import android.net.Uri
import java.util.*

interface RecordModel {
    val id: Int
    val plantId: Int
    val memo: String
    val pictureList: List<Uri>
    val createdTime: Date
}