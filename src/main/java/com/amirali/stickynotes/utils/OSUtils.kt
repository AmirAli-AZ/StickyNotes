package com.amirali.stickynotes.utils

import java.util.*

object OSUtils {

    fun get():OS {
        val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
        var os:OS = OS.UNKNOWN
        if (osName.contains("win")) {
            os = OS.WINDOWS
        } else if (osName.contains("nix") || osName.contains("nux")
            || osName.contains("aix")
        ) {
            os = OS.LINUX
        } else if (osName.contains("mac")) {
            os = OS.MAC
        }

        return os
    }

    enum class OS {
        WINDOWS, LINUX, MAC , UNKNOWN
    }
}