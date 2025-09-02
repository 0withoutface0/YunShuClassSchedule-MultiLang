package top.itning.yunshuclassschedule.ui.fragment.setting

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.*
import org.greenrobot.eventbus.EventBus
import top.itning.yunshuclassschedule.LocaleHelper
import top.itning.yunshuclassschedule.R
import top.itning.yunshuclassschedule.common.App
import top.itning.yunshuclassschedule.common.ConstantPool
import top.itning.yunshuclassschedule.entity.EventEntity
import top.itning.yunshuclassschedule.util.DateUtils.getNextMondayOfTimeInMillis
import top.itning.yunshuclassschedule.util.ThemeChangeUtil

/**
 * 设置Fragment
 *
 * @author itning
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefs: SharedPreferences
    private lateinit var defaultShowMainFragmentListPreference: ListPreference
    private lateinit var classReminderUpTime: ListPreference
    private lateinit var classReminderDownTime: ListPreference
    private lateinit var phoneMuteBeforeTime: ListPreference
    private lateinit var phoneMuteAfterTime: ListPreference
    private lateinit var nowWeekNumEditTextPreference: EditTextPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.registerOnSharedPreferenceChangeListener(this)
        val bundle = arguments
        if (bundle == null) {
            defaultShowMainFragmentListPreference = findPreference(DEFAULT_SHOW_MAIN_FRAGMENT)!!
            defaultShowMainFragmentListPreference.summary = defaultShowMainFragmentListPreference.entry
            val foregroundServiceStatus: Preference = findPreference<SwitchPreference>(FOREGROUND_SERVICE_STATUS)!!
            foregroundServiceStatus.setOnPreferenceChangeListener { _, newValue ->
                if (!(newValue as Boolean)) {
                    AlertDialog.Builder(requireContext()).setTitle(getString(R.string.notice_title))
                            .setMessage(getString(R.string.notice_message_disable_service))
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.notice_got_it), null)
                            .show()
                }
                true
            }
            nowWeekNumEditTextPreference = findPreference(NOW_WEEK_NUM)!!
            nowWeekNumEditTextPreference.setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString() == "" || (Integer.valueOf(newValue.toString())) > 50) {
                    Toast.makeText(requireContext(), getString(R.string.error_invalid_weeknum), Toast.LENGTH_LONG).show()
                    false
                } else {
                    true
                }
            }
            nowWeekNumEditTextPreference.summary = getString(R.string.label_weeknum_summary, prefs.getString(NOW_WEEK_NUM, "1"))
        } else {
            when (bundle.getString(ARG_PREFERENCE_ROOT)) {
                "class_reminder" -> {
                    classReminderUpTime = findPreference(CLASS_REMINDER_UP_TIME)!!
                    classReminderDownTime = findPreference(CLASS_REMINDER_DOWN_TIME)!!
                    classReminderUpTime.summary = classReminderUpTime.entry
                    classReminderDownTime.summary = classReminderDownTime.entry
                }
                "phone_mute" -> {
                    phoneMuteBeforeTime = findPreference(PHONE_MUTE_BEFORE_TIME)!!
                    phoneMuteAfterTime = findPreference(PHONE_MUTE_AFTER_TIME)!!
                    val phoneMuteStatus = findPreference<SwitchPreference>(PHONE_MUTE_STATUS)!!
                    phoneMuteStatus.setOnPreferenceChangeListener { _, newValue ->
                        if (newValue as Boolean) {
                            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted) {
                                Toast.makeText(requireContext(), getString(R.string.toast_grant_dnd_permission), Toast.LENGTH_LONG).show()
                                Toast.makeText(requireContext(), getString(R.string.toast_reenable_auto_silent), Toast.LENGTH_LONG).show()
                                startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                                return@setOnPreferenceChangeListener false
                            } else {
                                return@setOnPreferenceChangeListener true
                            }
                        }
                        true
                    }
                    phoneMuteBeforeTime.summary = phoneMuteBeforeTime.entry
                    phoneMuteAfterTime.summary = phoneMuteAfterTime.entry
                }
            }
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "on Destroy View")
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroyView()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val bundle = arguments
        if (bundle == null) {
            setPreferencesFromResource(R.xml.preference_settings, rootKey)
        } else {
            setPreferencesFromResource(R.xml.preference_settings, bundle.getString(ARG_PREFERENCE_ROOT))
        }
        //Changes locale to user defined one.
        val langPref = findPreference<androidx.preference.ListPreference>("app_language")
        langPref?.setOnPreferenceChangeListener { _, newValue ->
            LocaleHelper.saveLang(requireContext(), newValue as String)
            //requireActivity().recreate()
            // 2) Restart the whole task so every Activity/Fragment reloads with the new locale
            val ctx = requireActivity()
            val launch = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            if (launch != null) {
                startActivity(launch)
                ctx.overridePendingTransition(0, 0) // no animation
            }
            true
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            DEFAULT_SHOW_MAIN_FRAGMENT -> {
                defaultShowMainFragmentListPreference.summary = defaultShowMainFragmentListPreference.entry
            }
            CLASS_REMINDER_UP_TIME -> {
                classReminderUpTime.summary = classReminderUpTime.entry
            }
            CLASS_REMINDER_DOWN_TIME -> {
                classReminderDownTime.summary = classReminderDownTime.entry
            }
            PHONE_MUTE_BEFORE_TIME -> {
                phoneMuteBeforeTime.summary = phoneMuteBeforeTime.entry
            }
            PHONE_MUTE_AFTER_TIME -> {
                phoneMuteAfterTime.summary = phoneMuteAfterTime.entry
            }
            APP_COLOR_PRIMARY -> {
                ThemeChangeUtil.changeColor()
            }
            APP_COLOR_PRIMARY_DARK -> {
                ThemeChangeUtil.changeColor()
            }
            APP_COLOR_ACCENT -> {
                ThemeChangeUtil.changeColor()
            }
            APP_COLOR_PROGRESS -> {
                ThemeChangeUtil.changeColor()
            }
            NOW_WEEK_NUM -> {
                nowWeekNumEditTextPreference.summary = sharedPreferences.getString(key, "1")
                App.sharedPreferences.edit().putLong(ConstantPool.Str.NEXT_WEEK_OF_MONDAY.get(), getNextMondayOfTimeInMillis()).apply()
                EventBus.getDefault().post(EventEntity(ConstantPool.Int.TIME_TICK_CHANGE, ""))
            }
        }
    }

    companion object {
        private const val TAG = "SettingsFragment"
        const val CLASS_REMINDER_UP_TIME = "class_reminder_up_time"
        const val CLASS_REMINDER_DOWN_TIME = "class_reminder_down_time"
        const val PHONE_MUTE_STATUS = "phone_mute_status"
        const val PHONE_MUTE_BEFORE_TIME = "phone_mute_before_time"
        const val PHONE_MUTE_AFTER_TIME = "phone_mute_after_time"
        const val DEFAULT_SHOW_MAIN_FRAGMENT = "default_show_main_fragment"
        const val APP_COLOR_PRIMARY = "app_color_primary"
        const val APP_COLOR_PRIMARY_DARK = "app_color_primary_dark"
        const val APP_COLOR_ACCENT = "app_color_accent"
        const val APP_COLOR_PROGRESS = "app_color_progress"
        const val FOREGROUND_SERVICE_STATUS = "foreground_service_status"
        const val NOW_WEEK_NUM = "now_week_num"
    }
}
