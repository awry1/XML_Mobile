package com.seriouscompany.xmlmobile

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors
import com.google.android.material.R.attr.colorPrimary
import com.google.android.material.R.attr.colorSecondary

fun getPrimaryColor(context: Context, defaultColor: Int = Color.BLACK): Int {
    return MaterialColors.getColor(context, colorPrimary, defaultColor)
}

fun getSecondaryColor(context: Context, defaultColor: Int = Color.BLACK): Int {
    return MaterialColors.getColor(context, colorSecondary, defaultColor)
}

fun getValueColor(context: Context): Int {
    return ContextCompat.getColor(context, R.color.value)
}

fun getNumberingColor(context: Context): Int {
    return ContextCompat.getColor(context, R.color.numbering)
}