package com.mycampus.billingapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class UserDetails(
    var name: String = "",
    var email: String = "",
    var address: String = "",
    var mobile: String = "",
    var GST: String = "",
    var website: String = ""
)
