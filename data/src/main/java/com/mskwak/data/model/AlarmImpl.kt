package com.mskwak.data.model

import com.mskwak.domain.model.Alarm
import java.time.LocalTime

data class AlarmImpl(
    override val time: LocalTime,
    override val onOff: Boolean
) : Alarm
