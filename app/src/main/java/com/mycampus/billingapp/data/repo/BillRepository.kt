package com.mycampus.billingapp.data.repo

import android.util.Log
import com.mycampus.billingapp.data.room.RoomDao
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BillRepository @Inject constructor(
    private val billingDao : RoomDao
) {
    fun getAllItemCollectionsWithFeeItems(): Flow<List<BillItemCollectionWithBillItems>> {
        return billingDao.getAllItemCollectionsWithBillItems()
    }

    suspend fun addItemCollectionWithFeeItems(
        billItemCollection: BillItemCollection,
        billItems: List<BillItem>
    ) : Boolean {
        return try {
            val itemCollectionId = billingDao.insertBillItemCollection(billItemCollection)
            billItems.forEach { billItem ->
                billItem.bill_info_id = itemCollectionId
                billingDao.insertBillItem(billItem)
            }
            true
        } catch (e: Exception) {
            Log.d("Exception",e.localizedMessage)
            false
        }
    }
    suspend fun deleteBillItemCol(itemCollection: BillItemCollection):Boolean{
        return try {
            billingDao.deleteBillItemCol(itemCollection)
            true
        }catch (e : Exception){
            Log.d("Delete Exception",e.localizedMessage)
            false
        }
    }
}