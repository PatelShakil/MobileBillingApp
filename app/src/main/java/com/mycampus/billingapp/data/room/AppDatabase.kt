package com.mycampus.billingapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.CustomerItem

@Database(entities = [BillItemCollection::class, BillItem::class,CustomerItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun billingDao(): RoomDao
}
