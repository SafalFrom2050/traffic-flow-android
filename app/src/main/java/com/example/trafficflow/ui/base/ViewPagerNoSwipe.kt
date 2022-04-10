package com.example.trafficflow.ui.base

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class ViewPagerNoSwipe(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    private val isPagingEnabled = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (isPagingEnabled) super.onTouchEvent(ev) else false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (isPagingEnabled) super.onInterceptTouchEvent(ev) else false
    }

    override fun executeKeyEvent(event: KeyEvent): Boolean {
        return if (isPagingEnabled) super.executeKeyEvent(event) else false
    }
}