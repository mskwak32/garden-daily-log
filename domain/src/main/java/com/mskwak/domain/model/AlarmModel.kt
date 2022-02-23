package com.mskwak.domain.model

interface AlarmModel {
    val hour: Int      //0~23
    val minute: Int
    val onOff: Boolean
}
