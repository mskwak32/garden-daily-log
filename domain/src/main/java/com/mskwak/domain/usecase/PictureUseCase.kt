package com.mskwak.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.mskwak.domain.repository.PictureRepository
import javax.inject.Inject

class PictureUseCase @Inject constructor(
    private val pictureRepository: PictureRepository
) {

    suspend fun savePicture(bitmap: Bitmap): Uri {
        return pictureRepository.savePlantPicture(bitmap)
    }

    suspend fun deletePicture(vararg uri: Uri) {
        uri.forEach { pictureRepository.deletePlantPicture(it) }
    }
}