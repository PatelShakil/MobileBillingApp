package com.mycampus.billingapp.data.repo

import android.content.SharedPreferences
import com.google.gson.Gson
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.data.room.RoomDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

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
