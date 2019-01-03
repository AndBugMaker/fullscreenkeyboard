package com.example.jiangzhiguo.myapplication

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var isSoftKeboardShow = false
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.TRANSPARENT
        //        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
        //                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setFullScreen()
        addKeyBoardListener()
        background.setOnClickListener {
            if (isSoftKeboardShow) {
                hideSoftInput()
            } else {
                requestedOrientation = if (isProtraint()) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }
        }
    }

    private fun hideSoftInput() {
        if (currentFocus != null) {
            val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.hideSoftInputFromWindow(currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun setFullScreen() {
        val options = if (isProtraint()) {
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        window.decorView.systemUiVisibility = options
    }

    private fun addKeyBoardListener() {
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            //获取当前屏幕内容的高度
            val screenHeight = window.decorView.height
            //获取View可见区域的bottom
            val rect = Rect()
            //DecorView即为activity的顶级view
            window.decorView.getWindowVisibleDisplayFrame(rect)
            //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
            //选取screenHeight*2/3进行判断
            if (screenHeight * 2 / 3 > rect.bottom) {
                if (!isSoftKeboardShow) {
                    isSoftKeboardShow = true
                    onSoftKeyboardStateChanged(isSoftKeboardShow, screenHeight - rect.bottom - if (isProtraint()) StatusBarUtil.getNavigationBarHeight(this, true) else 0)

                }
            } else {
                if (isSoftKeboardShow) {
                    isSoftKeboardShow = false
                    onSoftKeyboardStateChanged(isSoftKeboardShow, 0)
                }
            }
        }
    }

    private fun onSoftKeyboardStateChanged(softKeboardShow: Boolean, i: Int) {
        setEditTextBottomMargin(softKeboardShow, i)
        if (!softKeboardShow) {
            setFullScreen()
        }
    }

    private fun setEditTextBottomMargin(softKeboardShow: Boolean, i: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(container)
        constraintSet.constrainWidth(et.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.connect(et.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
        constraintSet.connect(et.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
        constraintSet.constrainHeight(et.id, DisplayUtil.dp2px(this, 60.toFloat()))
        constraintSet.connect(et.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, i)
        constraintSet.applyTo(container)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        setFullScreen()
    }

    private fun isProtraint(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }
}
