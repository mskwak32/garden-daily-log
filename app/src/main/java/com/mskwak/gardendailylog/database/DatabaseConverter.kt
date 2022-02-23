package com.mskwak.gardendailylog.database

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*

class DatabaseConverter {
    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun timestampToDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    @TypeConverter
    fun uriToPath(uri: Uri?): String? {
        return uri?.path
    }

    @TypeConverter
    fun pathToUri(path: String?): Uri? {
        return path?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun uriListToJson(uriList: List<Uri>): String {
        return Gson().toJson(uriList.map { it.path })
    }

    @TypeConverter
    fun jsonToUriList(json: String): List<Uri> {
        return Gson().fromJson(json, Array<String>::class.java).toList().map { Uri.parse(it) }
    }
}