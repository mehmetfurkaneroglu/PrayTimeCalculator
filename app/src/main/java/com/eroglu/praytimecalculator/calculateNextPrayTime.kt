package com.eroglu.praytimecalculator

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun calculateNextPrayTime(jsonString: String): Pair<Date, String>? {
    val gson = Gson()
    val prayTimeData = gson.fromJson(jsonString, PrayTimeData::class.java)
    val prayTimes: List<PrayTime> = prayTimeData.data

    val now = Date()

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) //SimpleDateFormat zaman dilimi/locale ve DST (yaz/kış saati) konularında dikkat ister.
//    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Bugünün tarihi
    val todayStr = dateFormatter.format(now)
    val today = dateFormatter.parse(todayStr)

    val todayRecord = prayTimes.firstOrNull { it.tarih == todayStr }

    if (todayRecord != null) {
        val next = findNextPrayerInDay(todayRecord, now, dateTimeFormatter)
        if (next != null) return next
    }

    val nextRecord = prayTimes
        .filter {
            val recDate = dateFormatter.parse(it.tarih)
            !recDate.before(today) // bugün ve sonrası
//            recDate.after(today) // sadece bugünden SONRA
        }
        .sortedBy { dateFormatter.parse(it.tarih).time }
        .getOrNull(1)   // bugün 0. index, yarın 1. index
//        .firstOrNull() // en yakın gelecek gün, gün atlama olan yerlerde

    if (nextRecord != null) {
        val dateTime = dateTimeFormatter.parse("${nextRecord.tarih} ${nextRecord.imsak}")
        return dateTime to "İmsak"
    }

    return null
}

private fun findNextPrayerInDay(
    record: PrayTime,
    now: Date,
    dateTimeFormatter: SimpleDateFormat
): Pair<Date, String>? {

    val events = listOf(
        "İmsak" to record.imsak,
        "Sabah" to record.sabah,
        "Güneş" to record.gunes,
        "Öğle" to record.ogle,
        "İkindi" to record.ikindi,
        "Akşam" to record.aksam,
        "Yatsı" to record.yatsi
    )

    val dateList = mutableListOf<Pair<String, Date>>()

    for ((name, timeString) in events) {
        if (timeString.isNotBlank()) {
            val dt = dateTimeFormatter.parse("${record.tarih} $timeString")
            dateList.add(name to dt)
        }
    }

    val sorted = dateList.sortedBy { it.second.time }

    for ((name, dt) in sorted) {
        if (dt.after(now)) {
            return dt to name
        }
    }

    return null
}