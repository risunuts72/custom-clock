package io.github.risunuts72.customclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.risunuts72.customclock.ui.theme.CustomClockTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			CustomClockTheme {
				Surface(modifier = Modifier.fillMaxSize()) {
					AppRoot()
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
	val navController = rememberNavController()
	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val scope = rememberCoroutineScope()

	var updateDateMinute by remember { mutableFloatStateOf(240f) }

	ModalNavigationDrawer(
		drawerState = drawerState,
		drawerContent = {
			ModalDrawerSheet {
				Spacer(Modifier.height(12.dp))
				NavigationDrawerItem(
					label = { Text(text = "時計") },
					selected = false,
					onClick = {
						navController.navigate("clock")
						scope.launch { drawerState.close() }
					}
				)
				NavigationDrawerItem(
					label = { Text(text = "設定") },
					selected = false,
					onClick = {
						navController.navigate("settings")
						scope.launch { drawerState.close() }
					}
				)
			}
		}
	) {
		Scaffold(
			topBar = {
				TopAppBar(
					title = { Text(text = "時計アプリ") },
					navigationIcon = {
						IconButton(onClick = { scope.launch { drawerState.open() } }) {
							Icon(Icons.Default.Menu, contentDescription = "メニュー")
						}
					}
				)
			}
		) {
			innerPadding ->
			NavHost(
				navController = navController,
				startDestination = "clock",
				modifier = Modifier.padding(innerPadding)
			) {
				composable("clock") {
					ClockScreen(updateDateMinute = updateDateMinute.toLong(), modifier = Modifier.fillMaxSize())
				}
				composable("settings") {
					SettingsScreen(
						updateDateMinute = updateDateMinute,
						onUpdateDateMinuteChange = { updateDateMinute = it },
					)
				}
			}
		}
	}
}

/**
 * 時計の画面。
 */
@Composable
fun ClockScreen(updateDateMinute: Long, modifier: Modifier = Modifier) {
	var dateString by remember { mutableStateOf("yyyy-MM-dd") }
	var timeString by remember { mutableStateOf("hh:mm:ss") }
	// var timeString by remember { mutableStateOf("hh:mm:ss.SSS") }

	// 1sec毎に時刻を更新する。
	// `delay(1000L.milliseconds)` だと若干のズレが重なって最大1秒程度ずれる可能性があるため、
	// 900msec + 10msecループ を組んでいる。
	LaunchedEffect(Unit) {
		var currentTime = LocalDateTime.now()
		var sec = currentTime.second - 1
		while (true) {
			while (true) {
				currentTime = LocalDateTime.now()
				if (currentTime.second != sec) {
					sec = currentTime.second
					// 4:00を境に日付を変更する。
					val (datestr, timestr) = FormatDateTime(currentTime, updateDateMinute)
					dateString = datestr
					timeString = timestr
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

fun FormatDateTime(dateTime: LocalDateTime, updateDateMinute: Long): Pair<String, String> {
	val curDay = dateTime.dayOfMonth
	lateinit var dateString: String
	lateinit var timeString: String
	if (dateTime.minusMinutes(updateDateMinute).dayOfMonth != curDay) {
		dateString = "${"%04d".format(dateTime.year)}-${"%02d".format(dateTime.monthValue)}-${"%02d".format(dateTime.dayOfMonth-1)}"
		timeString = "${"%02d".format(dateTime.hour+24)}:${"%02d".format(dateTime.minute)}:${"%02d".format(dateTime.second)}"
		// timeString = "${"%02d".format(dateTime.hour+24)}:${"%02d".format(dateTime.minute)}:${"%02d".format(dateTime.second)}.${"%03d".format(dateTime.nano/1000000)}"
	} else {
		dateString = "${"%04d".format(dateTime.year)}-${"%02d".format(dateTime.monthValue)}-${"%02d".format(dateTime.dayOfMonth)}"
		timeString = "${"%02d".format(dateTime.hour)}:${"%02d".format(dateTime.minute)}:${"%02d".format(dateTime.second)}"
		// timeString = "${"%02d".format(dateTime.hour)}:${"%02d".format(dateTime.minute)}:${"%02d".format(dateTime.second)}.${"%03d".format(dateTime.nano/1000000)}"
	}
	return Pair(dateString, timeString)
}

/**
 * 設定画面。
 */
@Composable
fun SettingsScreen(updateDateMinute: Float, onUpdateDateMinuteChange: (Float) -> Unit) {

	Column (
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(
			text = "日付変更時刻：${formatMinutesToTime(updateDateMinute.toLong())}",
			style = MaterialTheme.typography.bodyLarge,
		)
		Spacer(modifier = Modifier.height(8.dp))
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth(),
		) {
			Text(
				text = "0:00",
				style = MaterialTheme.typography.labelSmall,
			)
			Slider(
				value = updateDateMinute,
				onValueChange = onUpdateDateMinuteChange,
				valueRange = 0f..720f,
				steps = 11,
			)
			Text(
				text = "6:00",
				style = MaterialTheme.typography.labelSmall,
			)
		}
	}
}

fun formatMinutesToTime(totalMinutes: Long): String {
	val hours = totalMinutes / 60
	val minutes = totalMinutes % 60
	return "%02d:%02d".format(hours, minutes)
}