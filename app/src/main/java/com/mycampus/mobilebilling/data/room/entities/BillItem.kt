package com.mycampus.mobilebilling.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bill_item")
data class BillItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var bill_info_id : Long,
    var item_name: String,
    var item_amount: Double,
    var creation_date : Long,
    var created_by : String
)
