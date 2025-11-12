package com.eroglu.praytimecalculator

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eroglu.praytimecalculator.ui.theme.PrayTimeCalculatorTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrayTimeCalculatorTheme {
                DateTimePickerComponent()
            }
        }
    }
}

@Composable
fun DateTimePickerComponent() { // ekrandaki tarih ve saat fonksiyonu
    val context = LocalContext.current // Android sistemiyle etkileşim (örneğin assets klasörüne erişmek için)
    val calendar = Calendar.getInstance() // tarih/saat işlemleri yapmak için
    var selectedDate by remember { mutableStateOf(calendar.time) } // ekranın otomatik yenilenmesi (recomposition) [Bu değeri hatırla ve değişirse UI’ı yeniden çiz]
    var nextPrayTime by remember { mutableStateOf<Pair<Date, String>?>(null) } // mutableStateOf: değiştirilebilir bir state (durum) oluşturur / remember → bu durumu Compose’un “hafızasında” saklar
    // Pair<A, B> → iki değeri birlikte taşımak için kullanılan bir Kotlin veri yapısıdır
    // JSON dosyasını okuma
    val jsonString = context.assets.open("vakitler.json").bufferedReader().use { it.readText() }
    // .bufferedReader() → bu akışı “satır satır okuyabilen” bir BufferedReader’a çeviriyor
    // .use { it.readText() } → dosyanın tamamını okuyup metin haline getiriyor.
    // use { ... } bloğu bitince dosya otomatik kapanıyor

    val datePickerDialog = DatePickerDialog( // DatePickerDialog → kullanıcıdan tarih alır
        context, // Dialog’u nerede göstereceğini bilmesi için
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            val timePickerDialog = TimePickerDialog( // TimePickerDialog → kullanıcıdan saat alır.
                context,
                { _, hourOfDay: Int, minute: Int ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    selectedDate = calendar.time
                    nextPrayTime = calculateNextPrayTime(jsonString, selectedDate)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // true verirsen hourOfDay 0..23 aralığında gelir. false ise AM/PM dönüşümü gerekir.
            )
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH), //month değeri 0 ile 11 arasıdır.
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Seçilen Zaman: ${SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(selectedDate)}") // Locale.getDefault()) cihaza göre tarih saat
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { datePickerDialog.show() }) {
            Text("Tarih ve Saat Seç")
        }
        Spacer(modifier = Modifier.height(16.dp))
        nextPrayTime?.let {
            val formattedDate = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(it.first) // it.first  → Date nesnesi (örnek: 2025-11-12 18:05)
            Text(text = "Sonraki Vakit: $formattedDate, \"${it.second}\"") // it.second → String metni (örnek: "Akşam")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateTimePickerPreview() {
    PrayTimeCalculatorTheme {
        DateTimePickerComponent()
    }
}
