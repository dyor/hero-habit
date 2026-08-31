package com.dyor.habithero.util

class AppUtilImpl : AppUtil {

    override fun shareApp() {
        println("This is not supported on Web platform")
    }

    override fun openFeedbackMail() {
        println("This is not supported on Web platform")
    }

    override fun getAppName(): String {
        return "Habit Hero" // TODO Update name
    }

    override fun getAppVersionInfo(): String {
        return "1.0.0" // TODO Update version
    }
}
