package com.mycampus.billingapp.ui.nav


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Details : Screen("details")
    object Customer : Screen("customer")
    // Add more screens as needed
}
