package io.github.risunuts72.customclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.risunuts72.customclock.ui.theme.CustomClockTheme
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			CustomClockTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					ClockScreen(
						modifier = Modifier.padding(innerPadding).fillMaxSize()
					)
				}
			}
		}
	}
}

@Composable
fun ClockScreen(modifier: Modifier = Modifier) {
	var dateString by remember { mutableStateOf("yyyy-MM-dd") }
	var timeString by remember { mutableStateOf("hh:mm:ss") }
	// var timeString by remember { mutableStateOf("hh:mm:ss.SSS") }

	LaunchedEffect(Unit) {
		var currentTime = LocalDateTime.now()
		var sec = currentTime.second
		while (true) {
			while (true) {
				currentTime = LocalDateTime.now()
				if (currentTime.second != sec) {
					sec = currentTime.second
					if (currentTime.hour < 4) {
						dateString = "${"%04d".format(currentTime.year)}-${"%02d".format(currentTime.monthValue)}-${"%02d".format(currentTime.dayOfMonth-1)}"
						timeString = "${"%02d".format(currentTime.hour+24)}:${"%02d".format(currentTime.minute)}:${"%02d".format(currentTime.second)}"
						// timeString = "${"%02d".format(currentTime.hour+24)}:${"%02d".format(currentTime.minute)}:${"%02d".format(currentTime.second)}.${"%03d".format(currentTime.nano/1000000)}"
					} else {
						dateString = "${"%04d".format(currentTime.year)}-${"%02d".format(currentTime.monthValue)}-${"%02d".format(currentTime.dayOfMonth)}"
						timeString = "${"%02d".format(currentTime.hour)}:${"%02d".format(currentTime.minute)}:${"%02d".format(currentTime.second)}"
						// timeString = "${"%02d".format(currentTime.hour)}:${"%02d".format(currentTime.minute)}:${"%02d".format(currentTime.second)}.${"%03d".format(currentTime.nano/1000000)}"
					}
					break
				}
				delay(10L.milliseconds)
			}
			delay(900L.milliseconds)
		}
	}

	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		Column (
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Text(
				text = dateString,
				fontSize = 24.sp,
				fontWeight = FontWeight.Bold,
			)
			Text(
				text = timeString,
				fontSize = 48.sp,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(top = 4.dp)
			)
		}
	}
}