package com.mycampus.billingapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.data.repo.BillRepository
import com.mycampus.billingapp.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository,
private val billingRepository : BillRepository) : ViewModel() {

    fun saveUserDetails(userDetails: UserDetails) = viewModelScope.launch{
        userRepository.saveUserDetails(userDetails)
    }



    fun getUserDetails() :UserDetails?{
        return userRepository.getUserDetails()
    }
    val allItemCollections: LiveData<List<BillItemCollectionWithBillItems>> =
        billingRepository.getAllItemCollectionsWithFeeItems().asLiveData()

    fun addItemCollection(billItemCollection: BillItemCollection, feeItems: List<BillItem>) {
        viewModelScope.launch {
            billingRepository.addItemCollectionWithFeeItems(billItemCollection, feeItems)
        }
    }
}