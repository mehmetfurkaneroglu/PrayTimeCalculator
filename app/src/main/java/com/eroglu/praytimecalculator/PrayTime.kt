package com.eroglu.praytimecalculator

data class PrayTime(
    val bolge_id: Int,
    val tarih: String,
    val imsak: String,
    val sabah: String,
    val gunes: String,
    val israk: String,
    val ogle_kerahet: String,
    val ogle: String,
    val ikindi: String,
    val asani: String,
    val ikindi_kerahet: String?,
    val aksam: String,
    val aksam_kerahet: String?,
    val yatsi: String,
    val isani: String,
    val kible: String
)

data class PrayTimeData(val data: List<PrayTime>)