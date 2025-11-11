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
fun DateTimePickerComponent() {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    var nextPrayTime by remember { mutableStateOf<Pair<Date, String>?>(null) }
    val jsonString = context.assets.open("vakitler.json").bufferedReader().use { it.readText() }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay: Int, minute: Int ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    selectedDate = calendar.time
                    nextPrayTime = calculateNextPrayTime(jsonString, selectedDate)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Seçilen Zaman: ${SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(selectedDate)}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { datePickerDialog.show() }) {
            Text("Tarih ve Saat Seç")
        }
        Spacer(modifier = Modifier.height(16.dp))
        nextPrayTime?.let {
            val formattedDate = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(it.first)
            Text(text = "Sonraki Vakit: $formattedDate, \"${it.second}\"")
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
