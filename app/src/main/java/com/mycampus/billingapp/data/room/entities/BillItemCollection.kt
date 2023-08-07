package com.mycampus.billingapp.data.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "billitemcol")
data class BillItemCollection(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerid:Long,
    var bill_no : String,
    var bill_pay_mode : String,
    var tax : Double,
    var total_amount : Double,
    var paid_amount : Double,
    var balance_amount : Double,
    var discount : Double,
    var remarks : String,
    var creation_date : Long,
    var created_by : String,
    var is_sync :Boolean
)

data class BillItemCollectionWithBillItems(
    @Embedded val itemCollection: BillItemCollection,
    @Relation(
        parentColumn = "id",
        entityColumn = "bill_info_id"
    )
    @Relation(
        parentColumn = "customerid",
        entityColumn = "id"
    )
    val itemList: List<BillItem>
)

