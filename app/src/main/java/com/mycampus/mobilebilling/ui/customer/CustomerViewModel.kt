package com.mycampus.mobilebilling.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mycampus.mobilebilling.data.repo.CustomerRepository
import com.mycampus.mobilebilling.data.room.entities.CustomerItem
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