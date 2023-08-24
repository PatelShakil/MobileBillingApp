package com.mycampus.billingapp.data.models

import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.CustomerItem

data class BillItemCollectionExcel(
    val id: Long = 0,
    val customer: CustomerItem,
    var bill_no: String,
    var bill_pay_mode: String,
    var tax: Double,
    var total_amount: Double,
    var paid_amount: Double,
    var balance_amount: Double,
    var discount: Double,
    var remarks: String,
    var creation_date: Long,
    var bill_date : Long,
    var created_by: String,
    var itemList: List<BillItem>,
    var is_sync: Boolean
)