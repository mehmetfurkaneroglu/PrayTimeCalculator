package com.eroglu.praytimecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eroglu.praytimecalculator.ui.theme.PrayTimeCalculatorTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrayTimeCalculatorTheme {
                var nextPrayTime by remember { mutableStateOf<Pair<Date, String>?>(null) }

                LaunchedEffect(Unit) {
                    val jsonString = assets.open("vakitler.json").bufferedReader().use { it.readText() }
                    nextPrayTime = calculateNextPrayTime(jsonString)
                }
                PrayTimeScreen(nextPrayTime)
            }
        }
    }
}

@Composable
fun PrayTimeScreen(nextPrayTime: Pair<Date, String>?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (nextPrayTime != null) {
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(nextPrayTime.first)
            Text(text = "$formattedDate, \"${nextPrayTime.second}\"")
        } else {
            Text(text = "Bir sonraki namaz vakti hesaplanıyor...")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrayTimeScreenPreview() {
    PrayTimeCalculatorTheme {
        val sampleDate = Date()
        val samplePrayTime = sampleDate to "İkindi"
        PrayTimeScreen(nextPrayTime = samplePrayTime)
    }
}
