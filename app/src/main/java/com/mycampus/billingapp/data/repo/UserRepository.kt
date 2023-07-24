package com.mycampus.billingapp.data.repo

import android.content.SharedPreferences
import com.google.gson.Gson
import com.mycampus.billingapp.data.models.UserDetails

class UserRepository(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()
    private val keyUserDetails = "user_details"

    fun saveUserDetails(userDetails: UserDetails) {
        val userDetailsJson = gson.toJson(userDetails)
        sharedPreferences.edit().putString(keyUserDetails, userDetailsJson).apply()
    }

    fun getUserDetails(): UserDetails? {
        val userDetailsJson = sharedPreferences.getString(keyUserDetails, null)
        return if (userDetailsJson != null) {
            gson.fromJson(userDetailsJson, UserDetails::class.java)
        } else {
            null
        }
    }
}
