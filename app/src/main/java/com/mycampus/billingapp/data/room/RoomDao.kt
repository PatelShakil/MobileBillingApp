package com.mycampus.billingapp.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.data.room.entities.ContactItem
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactItem(item : ContactItem)

    @Query("SELECT * FROM contact_item")
    fun getAllContacts():Flow<List<ContactItem>>
    @Delete
    suspend fun deleteBillItemCol(itemCol: BillItemCollection)

    @Delete
    suspend fun deleteBillItem(item: BillItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBillItemCollection(billItemCollection: BillItemCollection): Long

    @Query("SELECT * FROM customer_item")
    fun getAllCustomers():Flow<List<CustomerItem>>

    @Query("SELECT * FROM billitemcol")
    fun getAllBillItemsCol():Flow<List<BillItemCollection>>

    @Query("SELECT * FROM bill_item")
    fun getAllBillItems():Flow<List<BillItem>>
    
    @Transaction
    @Query("SELECT * FROM billitemcol")
    fun getAllItemCollectionsWithBillItems(): Flow<List<BillItemCollectionWithBillItems>>


    // Add other CRUD operations as needed
}