package com.mskwak.gardendailylog.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mskwak.gardendailylog.MockPlantUtil
import com.mskwak.gardendailylog.data.GardeningRecord
import com.mskwak.gardendailylog.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlantTest : LocalDatabase() {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var plantDao: PlantDao
    private lateinit var recordDao: RecordDao

    @Before
    fun init() {
        plantDao = db.plantDao()
        recordDao = db.recordDao()
    }

    @Test
    fun recordDeleteWhenPlantDeletedTest() = runBlocking {
        val mockPlant = MockPlantUtil.mockPlant()
        plantDao.insertPlant(mockPlant)
        val plant = plantDao.observePlants().getOrAwaitValue().first().also {
            val mockRecords = listOf<GardeningRecord>(
                MockPlantUtil.mockRecord(it.plantId),
                MockPlantUtil.mockRecord(it.plantId)
            )
            mockRecords.forEach { record ->
                recordDao.insertRecord(record)
            }
        }
        var records = recordDao.observeRecordsByPlantId(plant.plantId).getOrAwaitValue()
        assert(records.isNotEmpty())

        plantDao.deletePlant(plant)
        records = recordDao.observeRecordsByPlantId(plant.plantId).getOrAwaitValue()

        assert(records.isEmpty())
    }
}