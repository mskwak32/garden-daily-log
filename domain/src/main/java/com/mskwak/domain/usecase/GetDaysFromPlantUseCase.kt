package com.mskwak.domain.usecase

import com.mskwak.domain.model.Plant
import java.time.LocalDate
import java.time.Period

class GetDaysFromPlantUseCase {

    operator fun invoke(plant: Plant): Period {
        val today = LocalDate.now()
        val plantDate = plant.createdDate

        if (!today.isAfter(plantDate)) {
            return Period.ofDays(0)
        }

        return Period.between(plantDate, today)
    }
}