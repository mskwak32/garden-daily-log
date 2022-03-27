package com.mskwak.data.source.local

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.orhanobut.logger.Logger
import java.io.File
import java.io.FileOutputStream

class FileDataSource(private val baseDir: File) {

    fun savePicture(dirPath: String, bitmap: Bitmap): Uri {
        val dir = File(baseDir, dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        var file = File(dir, "${bitmap.hashCode()}")

        //중복저장 피하기
        var postfixIndex = 0
        while (file.exists()) {
            file = File(dir, "${bitmap.hashCode()}_${++postfixIndex}")
        }

        var out: FileOutputStream? = null
        try {
            file.createNewFile()
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            Logger.d("save picture: ${file.toUri().path}")
        } catch (e: Exception) {
            Logger.e("save bitmap fail: ${e.message}")
        } finally {
            out?.flush()
            out?.close()
        }
        return file.toUri()
    }

    fun deletePicture(uri: Uri) {
        val file = File(uri.path!!)
        if (file.exists()) {
            file.delete()
            Logger.d("delete picture: ${uri.path}")
        }
    }

    companion object {
        const val PLANT_PICTURE_DIR = "plantPicture"
    }
}