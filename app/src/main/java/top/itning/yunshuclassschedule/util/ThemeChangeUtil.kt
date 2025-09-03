package top.itning.yunshuclassschedule.util

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.tabs.TabLayout
import org.greenrobot.eventbus.EventBus
import top.itning.yunshuclassschedule.R
import top.itning.yunshuclassschedule.common.App
import top.itning.yunshuclassschedule.common.ConstantPool
import top.itning.yunshuclassschedule.entity.EventEntity
import com.google.android.material.color.MaterialColors
import com.google.android.material.R as M3

/**
 * 主题更换工具类
 *
 * @author itning
 */
object ThemeChangeUtil {
    private const val TAG = "ThemeChangeUtil"

    var isChange = App.sharedPreferences.getBoolean("night_mode", false)
    private var defaultColorPrimary: Int = 0
    private var defaultColorPrimaryDark: Int = 0
    private var defaultColorAccent: Int = 0
    private var defaultColorProgress: Int = 0

    /**
     * 更换夜间模式
     *
     * @param activity [AppCompatActivity]
     */
    @Synchronized
    fun changeNightMode(@NonNull activity: AppCompatActivity) {
        isChange = !isChange
        App.sharedPreferences.edit().putBoolean("night_mode", isChange).apply()
        activity.startActivity(Intent(activity, activity.javaClass))
        activity.finish()
    }

    /**
     * 更新颜色事件
     */
    @Synchronized
    fun changeColor() {
        Log.d(TAG, "app color change , send event")
        EventBus.getDefault().post(EventEntity(ConstantPool.Int.APP_COLOR_CHANGE))
    }

    /**
     * 初始化有DrawerLayout的Activity的颜色
     *
     * @param activity     [AppCompatActivity]
     * @param drawerLayout [DrawerLayout]
     */
    fun initColor(activity: AppCompatActivity, drawerLayout: DrawerLayout) {
        // Let the theme (Monet) drive colors. If you still want a tinted bar, derive from roles:
        val primary = MaterialColors.getColor(activity, M3.attr.colorPrimary, 0)
        activity.supportActionBar?.setBackgroundDrawable(ColorDrawable(primary))

        // Optional: if you want a tinted status bar for the drawer screen:
        // val surface = MaterialColors.getColor(activity, M3.attr.colorSurface, 0)
        // StatusBarUtil.setColorForDrawerLayout(activity, drawerLayout, surface, 10)
    }


    /**
     * 更新主Activity主题
     *
     * @param activity [AppCompatActivity]
     */
    fun changeMainActivityTheme(activity: AppCompatActivity) {
        // Do not swap to custom themes; let DayNight + dynamic color handle it.
        // If you still want a colored ActionBar, derive it from the current theme:
        val primary = MaterialColors.getColor(activity, M3.attr.colorPrimary, 0)
        activity.supportActionBar?.setBackgroundDrawable(ColorDrawable(primary))
        // Avoid forcing transparent/status flags here; keep window managed by the theme.
    }


    /**
     * 简单设置主题
     *
     * @param activity [AppCompatActivity]
     */
    fun simpleSetTheme(@NonNull activity: AppCompatActivity) {
        //might need it for API compatibility
        //if (isChange) {
        //    activity.setTheme(R.style.AppTheme_NightTheme_Setting)
        //}
    }

    /**
     * 初始化默认颜色
     *
     * @param activity [AppCompatActivity]
     */
    private fun initDefaultColor(@NonNull activity: AppCompatActivity) {
        if (defaultColorPrimary != 0) {
            return
        }
        defaultColorPrimary = ContextCompat.getColor(activity, R.color.colorPrimary)
        defaultColorPrimaryDark = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
        defaultColorAccent = ContextCompat.getColor(activity, R.color.colorAccent)
        defaultColorProgress = ContextCompat.getColor(activity, R.color.color_progress)
    }

    /**
     * 更新设置页面主题
     *
     * @param activity [AppCompatActivity]
     */
    fun changeTheme(activity: AppCompatActivity) {
        // Keep a consistent ActionBar that follows Monet
        val primary = MaterialColors.getColor(activity, M3.attr.colorPrimary, 0)
        activity.supportActionBar?.setBackgroundDrawable(ColorDrawable(primary))

        // Optional: if you still want to tint the status bar slightly:
        // val surface = MaterialColors.getColor(activity, M3.attr.colorSurface, 0)
        // StatusBarUtil.setColor(activity, surface, 30)
    }


    /**
     * 设置TabLayout颜色
     *
     * @param context   [Context]
     * @param tabLayout [TabLayout]
     */
    fun setTabLayoutColor(context: Context, tabLayout: TabLayout) {
        val primary = MaterialColors.getColor(context, M3.attr.colorPrimary, 0)
        val onSurface = MaterialColors.getColor(context, M3.attr.colorOnSurface, 0)
        val surface = MaterialColors.getColor(context, M3.attr.colorSurface, 0)

        tabLayout.setBackgroundColor(surface)
        tabLayout.setSelectedTabIndicatorColor(primary)
        tabLayout.setTabTextColors(onSurface, primary)
    }

    /**
     * 设置背景Resource
     *
     * @param context [Context]
     * @param views   [View]
     */
    fun setBackgroundResources(context: Context, vararg views: View) {
        val surface = MaterialColors.getColor(context, M3.attr.colorSurface, 0)
        for (v in views) {
            if (v.id == R.id.view_center || v.id == R.id.view_top || v.id == R.id.view_bottom) continue
            v.setBackgroundColor(surface)
        }
    }


    /**
     * 设置进度条颜色
     *
     * @param context [Context]
     * @param view    [View]
     */
    // 1) Progress background should come from the theme, not prefs
    fun setProgressBackgroundResource(context: Context, view: View) {
        val primary = MaterialColors.getColor(context, M3.attr.colorPrimary, 0)
        view.setBackgroundColor(primary)
    }


    /**
     * 获取当前强调色颜色
     *
     * @param context [Context]
     * @return 颜色数值
     */
    // 2) Accent = secondary role from the current theme (Monet on 12+)
    @CheckResult
    @ColorInt
    fun getNowThemeColorAccent(context: Context): Int {
        return MaterialColors.getColor(context, M3.attr.colorSecondary, 0)
    }



    /**
     * 设置TextView 颜色
     *
     * @param context   [Context]
     * @param textViews [TextView]
     */
    fun setTextViewsColorByTheme(context: Context, vararg textViews: TextView) {
        val onSurface = MaterialColors.getColor(context, M3.attr.colorOnSurface, 0)
        for (tv in textViews) tv.setTextColor(onSurface)
    }

}
