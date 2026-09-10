package com.example.data

import android.content.Context
import com.example.R
import com.example.model.DhikrSection
import java.util.Locale

class ImageRepository(private val context: Context) {

    fun getSplashImageResId(): Int {
        val customResId = findDrawableId("initial_image")
        return if (customResId != 0) customResId else R.drawable.img_splash_initial
    }

    fun getFinalImageResId(): Int {
        val customResId = findDrawableId("final_image")
        return if (customResId != 0) customResId else R.drawable.img_final_bg
    }

    fun getImageResIdForStep(section: DhikrSection, index: Int): Int {
        val sectionPrefix = when (section) {
            DhikrSection.ALLAHU_AKBAR -> "section1"
            DhikrSection.ALHAMDULILLAH -> "section2"
            DhikrSection.SUBHANALLAH -> "section3"
        }

        // Try format: section1_01, section1_02, ...
        val formattedTwoDigit = String.format(Locale.US, "%s_%02d", sectionPrefix, index)
        var resId = findDrawableId(formattedTwoDigit)
        if (resId != 0) return resId

        // Try format: section1_1, section1_2, ...
        val formattedSingleDigit = String.format(Locale.US, "%s_%d", sectionPrefix, index)
        resId = findDrawableId(formattedSingleDigit)
        if (resId != 0) return resId

        // Fallback to section thematic background
        return when (section) {
            DhikrSection.ALLAHU_AKBAR -> R.drawable.img_section1_bg
            DhikrSection.ALHAMDULILLAH -> R.drawable.img_section2_bg
            DhikrSection.SUBHANALLAH -> R.drawable.img_section3_bg
        }
    }

    private fun findDrawableId(name: String): Int {
        return try {
            context.resources.getIdentifier(name, "drawable", context.packageName)
        } catch (_: Exception) {
            0
        }
    }
}
