package com.mycampus.billingapp.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.data.room.entities.CustomerItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customerItem: CustomerItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBillItemCol(itemCol : BillItemCollection)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBillItem(item : BillItem)
    @Query("DELETE FROM billitemcol WHERE id = $item")
    suspend fun deleteBillItemCol(itemCol: BillItemCollection)

    @Insert
    suspend fun insertBillItemCollection(billItemCollection: BillItemCollection): Long

    @Query("SELECT * FROM customer_item")
    fun getAllCustomers():Flow<List<CustomerItem>>

    @Transaction
    @Query("SELECT * FROM billitemcol")
    fun getAllItemCollectionsWithBillItems(): Flow<List<BillItemCollectionWithBillItems>>


    // Add other CRUD operations as needed
}