package com.mycampus.mobilebilling.ui.nav


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Details : Screen("details")
    object Customer : Screen("customer")
    object Contact : Screen("contact")
    object Setting : Screen("setting")

    object BackupRestore : Screen("backuprestore")
    // Add more screens as needed
}
