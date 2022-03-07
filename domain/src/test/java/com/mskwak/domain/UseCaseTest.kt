package com.mskwak.domain

import com.mskwak.domain.model.Plant
import com.mskwak.domain.repository.PlantRepository
import com.mskwak.domain.repository.RecordRepository
import com.mskwak.domain.usecase.GardenUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate

@RunWith(MockitoJUnitRunner::class)
class UseCaseTest {
    @Mock
    lateinit var plantRepository: PlantRepository

    @Mock
    lateinit var recordRepository: RecordRepository

    @Mock
    lateinit var plant: Plant
    private lateinit var useCase: GardenUseCase

    @Before
    fun init() {
        useCase = GardenUseCase(plantRepository, recordRepository)
    }

    @Test
    fun getRemainWateringDate_test() {
        //물주기 시점이 남은 경우
        var lastWateringDate = LocalDate.now().minusDays(2)
        `when`(plant.lastWateringDate).thenReturn(lastWateringDate)
        `when`(plant.waterPeriod).thenReturn(3)

        var days = useCase.getRemainWateringDate(plant)
        assert(days == 1)

        //이미 물줘야하는 시점이 지난경우
        lastWateringDate = LocalDate.now().minusDays(10)
        `when`(plant.lastWateringDate).thenReturn(lastWateringDate)

        days = useCase.getRemainWateringDate(plant)
        assert(days == 0)
    }

    @Test
    fun getDaysFromLastWatering_test() {
        //마지막으로 물 준 시점이 오늘 이전인 경우
        var lastWateringDate = LocalDate.now().minusDays(5)
        `when`(plant.lastWateringDate).thenReturn(lastWateringDate)

        var days = useCase.getDaysFromLastWatering(plant)
        assert(days == 5)

        //마지막으로 물 준 시점이 미래인경우
        lastWateringDate = LocalDate.now().plusDays(3)
        `when`(plant.lastWateringDate).thenReturn(lastWateringDate)

        days = useCase.getDaysFromLastWatering(plant)
        assert(days == 0)
    }

    @Test
    fun getDaysFromPlant_test() {
        val plantDate = LocalDate.now().minusDays(10)
        `when`(plant.createdDate).thenReturn(plantDate)

        val days = useCase.getDaysFromPlant(plant)
        assert(days.days == 10)
    }
}