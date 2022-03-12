package com.mskwak.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mskwak.data.MockPlantUtil
import com.mskwak.data.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlantDataTest : LocalDatabase() {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var plantDao: PlantDao
    private lateinit var diaryDao: DiaryDao

    @Before
    fun init() {
        plantDao = db.plantDao()
        diaryDao = db.diaryDao()
    }

    @Test
    fun recordDeleteWhenPlantDeletedTest() = runBlocking {
        val mockPlant = MockPlantUtil.mockPlant()
        plantDao.insertPlant(mockPlant)
        val plant = plantDao.observePlants().getOrAwaitValue().first().also {
            val mockDiaries = listOf(
                MockPlantUtil.mockDiary(it.id),
                MockPlantUtil.mockDiary(it.id)
            )
            mockDiaries.forEach { record ->
                diaryDao.insertDiary(record)
            }
        }
        var records = diaryDao.observeDiariesByPlantId(plant.id).getOrAwaitValue()
        assert(records.isNotEmpty())

        plantDao.deletePlant(plant)
        records = diaryDao.observeDiariesByPlantId(plant.id).getOrAwaitValue()

        assert(records.isEmpty())
    }
}