package com.dyor.habithero

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.dyor.habithero.root.App
import com.dyor.habithero.root.AppInitializer

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    AppInitializer.initialize {}
    ComposeViewport {
        App()
    }
}
