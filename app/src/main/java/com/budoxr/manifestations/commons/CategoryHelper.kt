package com.budoxr.manifestations.commons

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import com.budoxr.manifestations.ui.theme.contribution
import com.budoxr.manifestations.ui.theme.emotions
import com.budoxr.manifestations.ui.theme.grayLight
import com.budoxr.manifestations.ui.theme.health
import com.budoxr.manifestations.ui.theme.mindset
import com.budoxr.manifestations.ui.theme.passion
import com.budoxr.manifestations.ui.theme.purpose
import com.budoxr.manifestations.ui.theme.relationships
import com.budoxr.manifestations.ui.theme.spirituality
import com.budoxr.manifestations.ui.theme.wealth
import org.koin.core.component.KoinComponent
import com.budoxr.manifestations.R

class CategoryHelper(private val context: Context) : KoinComponent {

    fun getCategoryByName(name: String, context: Context): CATEGORIES? {
        val categories = context.resources.getStringArray(R.array.categories_array)
        return categories.find { it.lowercase() == name.lowercase() }?.let {
            CATEGORIES.valueOf(it.uppercase())
        } ?: run { null }

    }

    fun getCategoryColor(key: String): Color =
        when (getCategoryByName(key, context)) {
            CATEGORIES.HEALTH -> health
            CATEGORIES.WEALTH -> wealth
            CATEGORIES.RELATIONSHIPS -> relationships
            CATEGORIES.MINDSET -> mindset
            CATEGORIES.EMOTIONS -> emotions
            CATEGORIES.PURPOSE -> purpose
            CATEGORIES.PASSION -> passion
            CATEGORIES.CONTRIBUTION -> contribution
            CATEGORIES.SPIRITUALITY -> spirituality
            else -> grayLight
        }

}

enum class CATEGORIES(val key: String) {
    HEALTH("Health"),
    WEALTH("Wealth"),
    RELATIONSHIPS("Relationships"),
    MINDSET("Mindset"),
    EMOTIONS("Emotions"),
    PURPOSE("Purpose"),
    PASSION("Passion"),
    CONTRIBUTION("Contribution"),
    SPIRITUALITY("Spirituality")
}