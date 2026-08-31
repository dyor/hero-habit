package com.dyor.habithero

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dyor.habithero.root.App
import com.dyor.habithero.root.AppInitializer

fun main() {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%5\$s%6\$s%n")

    application {
        AppInitializer.initialize {}
        Window(
            onCloseRequest = ::exitApplication,
            title = "Habit Hero",
        ) {
            App()
        }
    }
}
