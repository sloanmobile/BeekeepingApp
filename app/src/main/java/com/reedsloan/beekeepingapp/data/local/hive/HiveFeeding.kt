package com.reedsloan.beekeepingapp.data.local.hive
//FEEDING / STORES
//HONEY STORES: HIGH, AVERAGE, LOW
//POLLEN: HIGH, AVERAGE, LOW
//HONEY B HEALTHY: Y / N
//MEGABEE: Y/N
//VITA FEED GOLD: Y/N
//SUGAR SYRUP: Y/N
data class HiveFeeding(
    val honeyStores: HoneyStores,
    val pollen: Pollen,
    val honeyBHealthy: Boolean,
    val megaBee: Boolean,
    val vitaFeedGold: Boolean,
    val sugarSyrup: Boolean,
)