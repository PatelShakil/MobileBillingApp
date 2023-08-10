package com.mycampus.billingapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mycampus.billingapp.data.models.UserDetails
import com.mycampus.billingapp.data.repo.BillRepository
import com.mycampus.billingapp.data.repo.CustomerRepository
import com.mycampus.billingapp.data.repo.UserRepository
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.BillItemCollectionWithBillItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
private val billingRepository : BillRepository,
private val customerRepository: CustomerRepository
) : ViewModel() {

    fun saveUserDetails(userDetails: UserDetails) = viewModelScope.launch{
        userRepository.saveUserDetails(userDetails)
    }


    fun getUserDetails() :UserDetails?{
        return userRepository.getUserDetails()
    }
    val allItemCollections: LiveData<List<BillItemCollectionWithBillItems>> =
        billingRepository.getAllItemCollectionsWithFeeItems().asLiveData()



    var insertResult = MutableStateFlow<Boolean>(false)
    var isInserted = MutableStateFlow<Boolean>(false)
    fun addItemCollection(billItemCollection: BillItemCollection, feeItems: List<BillItem>) {
        insertResult.value = true
        viewModelScope.launch {
            isInserted.value = billingRepository.addItemCollectionWithFeeItems(billItemCollection, feeItems)
            insertResult.value = false
        }
    }

    var deleteResult = MutableStateFlow<Boolean>(false)
    var deleteProcess = MutableStateFlow<Boolean>(false)
    fun deleteBillItemCol(itemCollection: BillItemCollectionWithBillItems){
        deleteProcess.value = true
        viewModelScope.launch {
            deleteResult.value = try{
                itemCollection.itemList.forEach {
                    deleteBillItem(it)
                }
                billingRepository.deleteBillItemCol(itemCollection.itemCollection)
            }catch(e : Exception){
                e.printStackTrace()
                false
            }
            deleteProcess.value = false
        }
    }
    private fun deleteBillItem(item: BillItem){
        viewModelScope.launch {
            deleteResult.value = billingRepository.deleteBillItem(item)
        }
    }
}