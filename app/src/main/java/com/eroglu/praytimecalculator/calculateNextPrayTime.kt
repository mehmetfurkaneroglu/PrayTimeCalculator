package com.eroglu.praytimecalculator

import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun calculateNextPrayTime(jsonString: String, from: Date): Pair<Date, String>? { // jsonString: assets/vakitler.json dosyasının metin hali / from: kullanıcı tarafından seçilen tarih-saat / Pair<Date, String> → yani hem vakit zamanı hem de vakit adı (örnek: “İkindi”)
    val gson = Gson() // Gson → Google’ın JSON’u Kotlin sınıfına dönüştüren kütüphanesi
    val prayTimeData = gson.fromJson(jsonString, PrayTimeData::class.java) // PrayTimeData → JSON’un ana yapısı
    val prayTimes: List<PrayTime> = prayTimeData.data // elimizde her gün için PrayTime nesneleri listesi olur

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // dateFormatter: sadece tarihi işler (“2025-11-12”)
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) // dateTimeFormatter: tarih + saati işler (“2025-11-12 12:30”)

    val fromDateStr = dateFormatter.format(from) // fromDateStr: “2025-11-12” gibi, saat kısmı yok.
    val fromDate = dateFormatter.parse(fromDateStr) // fromDate: bu tarih nesnesine dönüştürülüyor. (Ama saat sıfırlanıyor, yani günün başlangıcı.)

    val todayRecord = prayTimes.firstOrNull { it.tarih == fromDateStr } // JSON listesindeki bugüne ait veriyi buluyor. Örnek: "tarih": "2025-11-12"

    if (todayRecord != null) { // Bugünün vakitleri varsa findNextPrayerInDay() fonksiyonuna gönderiyor
        val next = findNextPrayerInDay(todayRecord, from, dateTimeFormatter) // O fonksiyon “şu andan sonraki en yakın vakti” bulursa onu döndürüyor
        if (next != null) return next
    }

    val nextRecord = prayTimes // Eğer bugün tüm vakitler geçtiyse → yarınki imsak
        .filter { // Tüm vakitleri filtreliyor → bugünden sonraki günleri buluyor.
            val recDate = dateFormatter.parse(it.tarih)
            !recDate.before(fromDate) // bugün ve sonrası
        }
        .sortedBy { dateFormatter.parse(it.tarih).time } // Sonra sıralıyor (bugün index 0, yarın index 1).
        .getOrNull(1)   // bugün 0. index, yarın 1. index / Yarınki kaydı alıyor.

    if (nextRecord != null) { // Yani eğer bugün geçtiyse → yarın sabah imsak vaktini döndür.
        val dateTime = dateTimeFormatter.parse("${nextRecord.tarih} ${nextRecord.imsak}")
        return dateTime to "İmsak"
    }

    return null
}

private fun findNextPrayerInDay( // Bu yardımcı fonksiyon, bugün için sıradaki vakti bulur
    record: PrayTime,
    now: Date,
    dateTimeFormatter: SimpleDateFormat
): Pair<Date, String>? {

    val events = listOf( // güne ait tüm namaz vakitleri ve adları.
        "İmsak" to record.imsak,
        "Sabah" to record.sabah,
        "Güneş" to record.gunes,
        "Öğle" to record.ogle,
        "İkindi" to record.ikindi,
        "Akşam" to record.aksam,
        "Yatsı" to record.yatsi
    )

    val dateList = mutableListOf<Pair<String, Date>>()

    // Bunları tarih nesnesine dönüştürür - Her birini “2025-11-12 12:15” gibi bir Date objesine çevirir.
    for ((name, timeString) in events) {
        if (timeString.isNotBlank()) {
            val dt = dateTimeFormatter.parse("${record.tarih} $timeString")
            dateList.add(name to dt)
        }
    }

    // Sıralar ve sıradaki vakti bulur
    val sorted = dateList.sortedBy { it.second.time }

    for ((name, dt) in sorted) {
        if (dt.after(now)) {
            return dt to name
        }
    }

    return null
}