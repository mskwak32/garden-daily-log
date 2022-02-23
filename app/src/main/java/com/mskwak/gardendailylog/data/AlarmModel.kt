package com.mskwak.gardendailylog.data

data class AlarmModel(
    val hour: Int,      //0~23
    val minute: Int,
    val onOff: Boolean
) {

    fun getTimeText(): String {
        val ampm = if (hour < 12) "오전" else "오후"
        val h = "%02d".format(if (hour == 0) 12 else if (hour <= 12) hour else hour - 12)
        val m = "%02d".format(minute)
        return "$ampm $h:$m"
    }
}
