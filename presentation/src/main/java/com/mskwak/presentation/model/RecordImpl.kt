package com.mskwak.presentation.model

import android.net.Uri
import com.mskwak.domain.model.Record
import java.time.LocalDate

data class RecordImpl(
    override val plantId: Int,
    override val memo: String,
    override val pictureList: List<Uri>?,
    override val createdDate: LocalDate,
    override val id: Int = 0
) : Record {

    constructor(record: Record) : this(
        record.plantId,
        record.memo,
        record.pictureList,
        record.createdDate,
        record.id
    )
}
