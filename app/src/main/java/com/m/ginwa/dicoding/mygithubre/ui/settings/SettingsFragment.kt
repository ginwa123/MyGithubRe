package com.m.ginwa.dicoding.mygithubre.ui.settings

import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.edit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.receiver.AlarmReceiver
import com.m.ginwa.dicoding.mygithubre.receiver.CALENDAR_HOUR_OF_DAY
import com.m.ginwa.dicoding.mygithubre.receiver.CALENDAR_MINUTE
import com.m.ginwa.dicoding.mygithubre.receiver.TIME_FORMAT
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


const val REMINDER_SETTING = "reminder"
const val TIME_REMINDER_SETTING = "time_reminder"
const val CHANGE_LANGUAGE_SETTING = "change_language"

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var simpleDateFormat: SimpleDateFormat
    private lateinit var calendar: Calendar

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setDefaultReminder()
        setLanguage()
        setReminder()
    }

    private fun setDefaultReminder() {
        simpleDateFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        calendar = Calendar.getInstance()

        // default reminder is 09:00 am
        var hour = sharedPreferences.getInt(CALENDAR_HOUR_OF_DAY, -1000)
        var minute = sharedPreferences.getInt(CALENDAR_MINUTE, -1000)
        if (hour == -1000 || minute == -1000) {
            sharedPreferences.edit {
                putInt(CALENDAR_HOUR_OF_DAY, 9)
                putInt(CALENDAR_MINUTE, 0)
                apply()
            }
            hour = 9
            minute = 0
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
    }

    private fun setReminder() {
        val alarmReceiver =
            AlarmReceiver()
        val reminder = findPreference<SwitchPreferenceCompat>(REMINDER_SETTING)
        val timeReminder = findPreference<Preference>(TIME_REMINDER_SETTING)
        if (reminder != null && reminder.isChecked) timeReminder?.isEnabled = true
        reminder?.setOnPreferenceClickListener {
            if (reminder.isChecked) {
                timeReminder?.isEnabled = true
                alarmReceiver.setRepeatingAlarm(requireContext(), calendar)
            } else {
                timeReminder?.isEnabled = false
                alarmReceiver.cancelAlarm(requireContext())
            }
            return@setOnPreferenceClickListener false
        }
        timeReminder?.setOnPreferenceClickListener {
            val hour = sharedPreferences.getInt(CALENDAR_HOUR_OF_DAY, 9)
            val minute = sharedPreferences.getInt(CALENDAR_MINUTE, 0)
            val mTimePicker = TimePickerDialog(
                requireContext(), { _, selectedHour, selectedMinute ->
                    sharedPreferences.edit {
                        putInt(CALENDAR_HOUR_OF_DAY, selectedHour)
                        putInt(CALENDAR_MINUTE, selectedMinute)
                        apply()
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                    calendar.set(Calendar.MINUTE, selectedMinute)
                    timeReminder.title = simpleDateFormat.format(calendar.time)
                    alarmReceiver.setRepeatingAlarm(requireContext(), calendar)
                },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle(getString(R.string.select_time))
            mTimePicker.show()
            return@setOnPreferenceClickListener false
        }
        timeReminder?.title = simpleDateFormat.format(calendar.time)

    }

    private fun setLanguage() {
        val language = findPreference<Preference>(CHANGE_LANGUAGE_SETTING)
        language?.summary = Locale.getDefault().displayName
        language?.setOnPreferenceClickListener {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
            return@setOnPreferenceClickListener false
        }
    }
}