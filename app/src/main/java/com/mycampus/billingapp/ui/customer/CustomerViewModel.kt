package com.mycampus.billingapp.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mycampus.billingapp.data.repo.CustomerRepository
import com.mycampus.billingapp.data.room.entities.CustomerItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val customerRepo : CustomerRepository
):ViewModel() {

    val allCustomers = customerRepo.getAllCustomers().asLiveData()

    fun addCustomer(customerItem: CustomerItem) = viewModelScope.launch{
        customerRepo.addCustomer(customerItem)
    }

}