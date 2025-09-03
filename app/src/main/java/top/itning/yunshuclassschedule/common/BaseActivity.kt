package top.itning.yunshuclassschedule.common

import android.content.Intent
import android.os.Bundle

import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import top.itning.yunshuclassschedule.LocaleHelper
import top.itning.yunshuclassschedule.entity.EventEntity
import top.itning.yunshuclassschedule.service.CommonService
import top.itning.yunshuclassschedule.service.RemindService
import top.itning.yunshuclassschedule.service.TodayWidgetService
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat



/**
 * Base App Activity
 *
 * @author itning
 */
abstract class BaseActivity : AppCompatActivity() {

    // Launcher for the permission dialog
    private val requestPostNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                // Optional: guide user to settings if denied
                // startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                //     putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                // })
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask for POST_NOTIFICATIONS if needed
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPostNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        startService(Intent(this, CommonService::class.java))
        startService(Intent(this, RemindService::class.java))
        startService(Intent(this, TodayWidgetService::class.java))
    }
    // Override locale
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyAppLocale(newBase))
    }

    /**
     * 消息事件
     *
     * @param eventEntity what
     */
    abstract fun onMessageEvent(eventEntity: EventEntity)
}
