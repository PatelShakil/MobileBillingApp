package com.mycampus.mobilebilling.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_item")
data class CustomerItem(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val name:String,
    val mobile:String,
    val email:String,
    val address:String
)
