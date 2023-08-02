package com.mycampus.billingapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection

@Database(entities = [BillItemCollection::class, BillItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun billingDao(): RoomDao
}
