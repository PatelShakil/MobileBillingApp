package com.mycampus.billingapp.data.repo

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mycampus.billingapp.data.models.UserDetails
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
) {
    //    val sp = context.applicationContext.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
    val sp = sharedPreferences
    private val gson = Gson()
    private val keyUserDetails = "user_details"

    fun saveUserDetails(userDetails: UserDetails) {
        val userDetailsJson = gson.toJson(userDetails)
        sharedPreferences.edit().putString(keyUserDetails, userDetailsJson).apply()
    }


    fun getUserDetails(): UserDetails? {
        val userDetailsJson = sp.getString(keyUserDetails, null)
        return if (userDetailsJson != null) {
            gson.fromJson(userDetailsJson, UserDetails::class.java)
        } else {
            null
        }
    }

}
