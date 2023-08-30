package com.mycampus.billingapp.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_item")
data class ContactItem(
    @PrimaryKey
    val id: String,
    val name: String,
    val mobileNo: String
)
