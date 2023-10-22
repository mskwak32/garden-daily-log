package com.mskwak.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface PictureRepository {
    suspend fun savePlantPicture(bitmap: Bitmap): Uri
    suspend fun deletePlantPicture(uri: Uri)
}