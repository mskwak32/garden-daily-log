package com.mskwak.domain.model

import android.net.Uri
import java.time.LocalDate

interface Diary {
    val id: Int
    val plantId: Int
    val memo: String
    val pictureList: List<Uri>?
    val createdDate: LocalDate
}