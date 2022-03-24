package com.mskwak.presentation.model

import android.net.Uri
import com.mskwak.domain.model.Diary
import java.time.LocalDate

data class DiaryImpl(
    override val plantId: Int,
    override val memo: String,
    override val pictureList: List<Uri>?,
    override val createdDate: LocalDate,
    override val id: Int = 0
) : Diary {

    constructor(diary: Diary) : this(
        diary.plantId,
        diary.memo,
        diary.pictureList,
        diary.createdDate,
        diary.id
    )
}
