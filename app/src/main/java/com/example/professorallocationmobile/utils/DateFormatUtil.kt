package com.example.professorallocationmobile.utils

class DateFormatUtil private constructor() {
    companion object {
        fun formatTime(inputTime: String): String {
            val timeFormatWithSeconds = Regex("""^([01]\d|2[0-3]):([0-5]\d):([0-5]\d)$""")
            val timeFormatWithoutSeconds = Regex("""^([01]\d|2[0-3]):([0-5]\d)$""")

            return when {
                timeFormatWithSeconds.matches(inputTime) -> inputTime.substring(0, 5)
                timeFormatWithoutSeconds.matches(inputTime) -> "$inputTime:00"
                else -> inputTime
            }
        }
    }
}