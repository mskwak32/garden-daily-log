package com.mskwak.presentation.model

import android.net.Uri
import com.mskwak.domain.model.Plant
import java.time.LocalDate

data class PlantImpl(
    override val name: String,
    override val createdDate: LocalDate,
    override val waterPeriod: Int,
    override val lastWateringDate: LocalDate,
    override val wateringAlarm: AlarmImpl,
    override val pictureUri: Uri?,
    override val memo: String?,
    override val id: Int = 0
) : Plant {


    constructor(plant: Plant) : this(
        plant.name,
        plant.createdDate,
        plant.waterPeriod,
        plant.lastWateringDate,
        AlarmImpl(plant.wateringAlarm),
        plant.pictureUri,
        plant.memo,
        plant.id
    )
}