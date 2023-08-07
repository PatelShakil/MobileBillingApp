package com.mycampus.billingapp.data.repo

import com.mycampus.billingapp.data.room.RoomDao
import com.mycampus.billingapp.data.room.entities.CustomerItem
import javax.inject.Inject

class CustomerRepository @Inject constructor(val billingDao: RoomDao) {

    fun getAllCustomers() = billingDao.getAllCustomers()

    suspend fun addCustomer(customerItem: CustomerItem){
        billingDao.insertCustomer(customerItem)
    }
}