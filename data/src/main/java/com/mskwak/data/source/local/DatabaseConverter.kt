package com.mskwak.data.source.local

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class DatabaseConverter {
    @TypeConverter
    fun dateToString(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun stringToDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }

    @TypeConverter
    fun timeToString(time: LocalTime): String {
        return time.toString()
    }

    @TypeConverter
    fun stringToTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString)
    }

    @TypeConverter
    fun dateTimeToString(dateTime: LocalDateTime): String {
        return dateTime.toString()
    }

    @TypeConverter
    fun stringToDateTime(dateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString)
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