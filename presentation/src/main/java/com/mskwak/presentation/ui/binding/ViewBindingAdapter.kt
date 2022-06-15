package com.mskwak.presentation.ui.binding

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.mskwak.presentation.R
import com.mskwak.presentation.ui.custom_component.TextViewWithIcon
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@BindingAdapter("fieldEmpty")
fun TextViewWithIcon.setFieldEmpty(isFieldEmpty: Boolean) {
    this.setFieldEmpty(isFieldEmpty)
}

@BindingAdapter("localMonth")
fun TextView.localMonthToText(localDate: LocalDate?) {
    val formatter = DateTimeFormatter.ofPattern("yyyy. MM")
    this.text = localDate?.format(formatter) ?: ""
}

@BindingAdapter("localDate")
fun TextView.localDateToText(localDate: LocalDate?) {
    val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    this.text = localDate?.format(format) ?: ""
}

@BindingAdapter("localTime")
fun TextView.localTimeToText(localTime: LocalTime?) {
    val format = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    this.text = localTime?.format(format) ?: ""
}

@BindingAdapter("wateringPeriod")
fun TextView.setWateringPeriod(period: Int) {
    this.text = if (period <= 0) context.getString(R.string.none) else context.getString(
        R.string.watering_period_unit,
        period
    )
}

@BindingAdapter("imageUri")
fun ImageView.setImageUri(uri: Uri?) {
    if (uri == null) {
        return
    }

    Glide.with(this)
        .load(File(uri.path!!))
        .into(this)
}

@SuppressLint("UseCompatLoadingForDrawables")
@BindingAdapter("thumbnailUri")
fun ImageView.setThumbnail(uri: Uri?) {
    if (uri == null) {
        Glide.with(this)
            .load(this.context.getDrawable(R.drawable.plant_default))
            .into(this)
    } else {
        Glide.with(this)
            .load(File(uri.path!!))
            .override(100)
            .into(this)
    }
}