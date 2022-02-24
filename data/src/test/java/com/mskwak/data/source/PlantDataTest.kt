package com.mskwak.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mskwak.data.MockPlantUtil
import com.mskwak.data.getOrAwaitValue
import com.mskwak.data.model.RecordData
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
            val mockRecords = listOf<RecordData>(
                MockPlantUtil.mockRecord(it.id),
                MockPlantUtil.mockRecord(it.id)
            )
            mockRecords.forEach { record ->
                recordDao.insertRecord(record)
            }
        }
        var records = recordDao.observeRecordsByPlantId(plant.id).getOrAwaitValue()
        assert(records.isNotEmpty())

        plantDao.deletePlant(plant)
        records = recordDao.observeRecordsByPlantId(plant.id).getOrAwaitValue()

        assert(records.isEmpty())
    }
}