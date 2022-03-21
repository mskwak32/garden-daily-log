package com.mskwak.presentation.model

import com.mskwak.domain.model.Alarm
import java.time.LocalTime

data class AlarmImpl(
    override val time: LocalTime,
    override val onOff: Boolean,
    override val alarmCode: Int
) : Alarm {
    constructor(alarm: Alarm) : this(alarm.time, alarm.onOff, alarm.alarmCode)
}
