package io.github.risunuts72.customclock

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private val UPDATE_DATE_MINUTES_KEY = floatPreferencesKey("update_date_minutes")

class ClockSettingsRepository(private val context: Context) {
	val updateDateMinuteFlow: Flow<Float> = context.dataStore.data.map {
		preferences -> preferences[UPDATE_DATE_MINUTES_KEY] ?: 240f
	}

	suspend fun saveUpdateDateMinutes(value: Float) {
		context.dataStore.edit {
			preferences -> preferences[UPDATE_DATE_MINUTES_KEY] = value
		}
	}
}