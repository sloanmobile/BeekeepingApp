package com.reedsloan.beekeepingapp.data.local.hive

//DISEASES & TREATMENTS
//HIVE HEALTH: SMALL HIVE BEETLES, VARROA MITES, ANTS, MOLD, WAX MOTH, CHALKBROOD, TRACHEAL MITES, NOSEMA, EFB, AFB
data class HiveHealth(
    val diseases: List<HiveDisease> = emptyList(),
    val treatments: List<HiveTreatment> = emptyList()
)
