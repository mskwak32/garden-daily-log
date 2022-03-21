package com.mskwak.presentation.ui.custom_component

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

class ZoomOutPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.apply {
            when {
                position <= 1 -> {
                    val scaleFactor = max(MIN_SCALE, 1 - abs(position))

                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
            }
        }
    }

    companion object {
        private const val MIN_SCALE = 0.9f
    }
}