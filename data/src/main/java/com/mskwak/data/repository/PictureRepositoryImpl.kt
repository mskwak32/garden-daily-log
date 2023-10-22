package com.mskwak.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.mskwak.data.source.local.FileDataSource
import com.mskwak.domain.repository.PictureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor(
    private val fileDataSource: FileDataSource,
) : PictureRepository {
    override suspend fun savePlantPicture(bitmap: Bitmap): Uri = withContext(Dispatchers.IO) {
        fileDataSource.savePicture(FileDataSource.PLANT_PICTURE_DIR, bitmap)
    }

    override suspend fun deletePlantPicture(uri: Uri) = withContext(Dispatchers.IO) {
        fileDataSource.deletePicture(uri)
    }
}