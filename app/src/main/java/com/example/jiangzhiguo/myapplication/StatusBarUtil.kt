package com.example.jiangzhiguo.myapplication

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.*

/**
 * Created by jiangzhiguo on 19/1/1.
 */

object StatusBarUtil {
    fun transparentStatusBar(activity: Activity) {
        val window = activity.window
        if (canHideStatusBar()) {//5.0及以上
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun canHideStatusBar(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    fun setLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flag = activity.window.decorView.systemUiVisibility
            activity.window.decorView.systemUiVisibility = flag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun showNavKey(context: Context, systemUiVisibility: Int) {
        (context as Activity).getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility)
    }

    fun hideNavKey(context: Context) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            val v = (context as Activity).window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = (context as Activity).window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }
    }


    fun setStatusBarColor(activity: Activity, colorResId: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = activity.resources.getColor(colorResId)
                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /**
     * 检测并切换沉浸模式(隐藏状态栏和底部的导航栏)
     */
    //获取是否存在NavigationBar
    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val rs = context.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {

        }

        return hasNavigationBar

    }


    fun isNavigationBarShow(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Build.BRAND.equals("Xiaomi")) { //小米用这个方法来判断导航栏是否显示
                val result = Settings.Global.getInt(context.contentResolver, "force_fsg_nav_bar", 0)
                return result == 0
            }
            val display = (context as Activity).windowManager.defaultDisplay
            val size = Point()
            val realSize = Point()
            display.getSize(size)
            display.getRealSize(realSize)
            return realSize.y !== size.y
        } else {
            val menu = ViewConfiguration.get(context).hasPermanentMenuKey()
            val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            return !(menu || back)
        }
    }

    fun getNavigationBarHeight(context: Context, fullScreen: Boolean): Int {
        val resources = context.resources

        val id = resources.getIdentifier(
                if (!fullScreen) "navigation_bar_height" else "navigation_bar_height_landscape",
                "dimen", "android")
        return if (id > 0) {
            resources.getDimensionPixelSize(id)
        } else 0
    }

}
