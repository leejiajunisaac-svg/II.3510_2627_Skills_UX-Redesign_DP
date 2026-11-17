package com.example.swapcampus_isaaclee.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
actual fun currentLanguageTag(): String {
    return LocalConfiguration.current.locales[0].toLanguageTag()
}

actual fun formatPrice(price: Double, languageTag: String): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        decimalSeparator = '.'
        groupingSeparator = ' ' // Use space for grouping if needed, or none
    }
    val formatter = DecimalFormat("0.00", symbols)
    return "€${formatter.format(price)}"
}

actual fun formatDate(date: LocalDate, languageTag: String): String {
    val locale = Locale.forLanguageTag(languageTag)
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    return date.toJavaLocalDate().format(formatter)
}
