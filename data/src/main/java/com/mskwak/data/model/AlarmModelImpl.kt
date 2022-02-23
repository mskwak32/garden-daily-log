package com.mskwak.data.model

import com.mskwak.domain.model.AlarmModel

data class AlarmModelImpl(
    override val hour: Int,
    override val minute: Int,
    override val onOff: Boolean
) : AlarmModel {

}
