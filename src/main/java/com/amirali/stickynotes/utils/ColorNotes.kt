package com.amirali.stickynotes.utils

enum class ColorNotes(val value: String, val colorName: String) {

    YELLOW("#CDA504", "Yellow"),
    GREEN("#5AA751", "Green"),
    PINK("#C873A6", "Pink"),
    PURPLE("#9F73CA", "Purple"),
    BLUE("#4FAACD", "Blue"),
    GRAY("#888888", "Gray"),
    CHARCOAL("#4B4B4B", "Charcoal");

    companion object {
        fun findByColorValue(colorValue: String?): ColorNotes? {
            when (colorValue) {
                "#CDA504" -> return YELLOW
                "#5AA751" -> return GREEN
                "#C873A6" -> return PINK
                "#9F73CA" -> return PURPLE
                "#4FAACD" -> return BLUE
                "#888888" -> return GRAY
                "#4B4B4B" -> return CHARCOAL
            }
            return null
        }
    }

}