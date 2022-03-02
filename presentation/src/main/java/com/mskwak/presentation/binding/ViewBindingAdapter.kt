package com.mskwak.presentation.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mskwak.presentation.R
import com.mskwak.presentation.custom_component.TextViewWithIcon
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@BindingAdapter("fieldEmpty")
fun TextViewWithIcon.setFieldEmpty(isFieldEmpty: Boolean) {
    this.setFieldEmpty(isFieldEmpty)
}

@BindingAdapter("localDate")
fun TextView.localDateToText(localDate: LocalDate) {
    val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    this.text = localDate.format(format)
}

@BindingAdapter("localTime")
fun TextView.localTimeToText(localTime: LocalTime) {
    val format = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    this.text = localTime.format(format)
}

@BindingAdapter("localDateTime")
fun TextView.localDateTimeToText(localDateTime: LocalDateTime) {
    val format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    this.text = localDateTime.format(format)
}

@BindingAdapter("wateringPeriod")
fun TextView.setWateringPeriod(period: Int) {
    this.text = if (period <= 0) context.getString(R.string.none) else context.getString(
        R.string.watering_period_unit,
        period
    )
}