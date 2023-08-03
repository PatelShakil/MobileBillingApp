package com.mycampus.billingapp.ui.nav


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Details : Screen("details")
    // Add more screens as needed
}
