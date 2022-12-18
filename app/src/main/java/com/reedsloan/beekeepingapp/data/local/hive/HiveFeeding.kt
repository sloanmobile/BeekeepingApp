package com.reedsloan.beekeepingapp.data.local.hive
//FEEDING / STORES
//HONEY STORES: HIGH, AVERAGE, LOW
//POLLEN: HIGH, AVERAGE, LOW
//HONEY B HEALTHY: Y / N
//MEGABEE: Y/N
//VITA FEED GOLD: Y/N
//SUGAR SYRUP: Y/N
data class HiveFeeding(
    val honeyStores: HoneyStores? = null,
    val pollen: Pollen? = null,
    val honeyBHealthy: Boolean = false,
    val megaBee: Boolean = false,
    val vitaFeedGold: Boolean = false,
    val sugarSyrup: Boolean = false,
)