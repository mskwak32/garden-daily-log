package com.mskwak.presentation.model

import com.mskwak.domain.model.Alarm
import java.time.LocalTime

data class AlarmUiData(
    override val time: LocalTime,
    override val onOff: Boolean
) : Alarm {
    constructor(alarm: Alarm) : this(alarm.time, alarm.onOff)
}
