package com.mskwak.data.model

import com.mskwak.domain.model.Alarm
import java.time.LocalTime

data class AlarmData(
    override val time: LocalTime,
    override val onOff: Boolean
) : Alarm {
    constructor(alarm: Alarm) : this(time = alarm.time, onOff = alarm.onOff)
}
