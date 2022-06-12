package com.mskwak.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mskwak.data.MockPlantUtil
import com.mskwak.data.getOrAwaitValue
import com.mskwak.data.model.DiaryData
import com.mskwak.data.model.PlantData
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class DaoTest : LocalDatabase() {
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
    fun getPlantNamesTest() = runBlocking {
        val mockPlants = mutableListOf<PlantData>()
        repeat(2) {
            val mock = MockPlantUtil.mockPlant()
            plantDao.insertPlant(mock)
            mockPlants.add(MockPlantUtil.mockPlant())
        }

        val plantNameMap = plantDao.getPlantNames()
        assert(plantNameMap.size == 2)
        assert(plantNameMap.containsValue(mockPlants[0].name))
    }

    @Test
    fun observeDiariesByMonthTest() = runBlocking {
        val diaries = mutableListOf<DiaryData>()
        val datePrevMonth = LocalDate.now().minusMonths(1)
        val dateNow = LocalDate.now()
        val dateNextMonth = LocalDate.now().plusMonths(1)

        diaries.add(MockPlantUtil.mockDiary(0, datePrevMonth))
        diaries.add(MockPlantUtil.mockDiary(0, dateNow))
        diaries.add(MockPlantUtil.mockDiary(1, dateNow))
        diaries.add(MockPlantUtil.mockDiary(0, dateNextMonth))

        diaries.forEach {
            diaryDao.insertDiary(it)
        }

        val minDate = dateNow.withDayOfMonth(1)
        val maxDate = dateNow.withDayOfMonth(dateNow.lengthOfMonth())

        var thisMonthDiaries = diaryDao.getDiariesByPlantId(0, minDate, maxDate).getOrAwaitValue()
        assert(thisMonthDiaries.size == 1)
        assert(thisMonthDiaries.first().createdDate == dateNow)

        thisMonthDiaries = diaryDao.getDiaries(minDate, maxDate).getOrAwaitValue()
        assert(thisMonthDiaries.size == 2)
    }
}