package com.mskwak.domain.model

import java.time.LocalTime

interface Alarm {
    val time: LocalTime
    val onOff: Boolean
}
