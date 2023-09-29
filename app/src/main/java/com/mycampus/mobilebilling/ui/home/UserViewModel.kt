package com.mycampus.mobilebilling.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mycampus.mobilebilling.data.models.UserDetails
import com.mycampus.mobilebilling.data.repo.BillRepository
import com.mycampus.mobilebilling.data.repo.ContactRepo
import com.mycampus.mobilebilling.data.repo.UserRepository
import com.mycampus.mobilebilling.data.room.entities.BillItem
import com.mycampus.mobilebilling.data.room.entities.BillItemCollection
import com.mycampus.mobilebilling.data.room.entities.BillItemCollectionWithBillItems
import com.mycampus.mobilebilling.data.room.entities.ContactItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val billingRepository: BillRepository,
    private val contactRepo : ContactRepo
) : ViewModel() {

    val allSyncContacts : LiveData<List<ContactItem>> = contactRepo.getAllContacts().asLiveData()

    fun saveContact(contact : ContactItem) = viewModelScope.launch{
        contactRepo.insertContact(contact)
    }
    fun saveContactList(contacts : List<ContactItem>) = viewModelScope.launch{
        contactRepo.insertContactList(contacts)
    }

    var contacts : LiveData<List<ContactItem>?>? = null
    fun loadContacts(context: Context) {
        contacts = contactRepo.loadContacts(context).asLiveData()
    }
    fun saveUserDetails(userDetails: UserDetails) = viewModelScope.launch {
        userRepository.saveUserDetails(userDetails)
    }

    var userDetails = MutableLiveData<UserDetails>()
    fun getUser() = viewModelScope.launch {
        userDetails.value = userRepository.getUserDetails()
    }
    init{
        getUser()
    }


    val allItemCollections: LiveData<List<BillItemCollectionWithBillItems>> =
        billingRepository.getAllItemCollectionsWithFeeItems().asLiveData()


    var insertResult = MutableStateFlow<Boolean>(false)
    var isInserted = MutableStateFlow<Boolean>(false)
    fun addItemCollection(billItemCollection: BillItemCollection, feeItems: List<BillItem>) {
        insertResult.value = true
        viewModelScope.launch {
            isInserted.value =
                billingRepository.addItemCollectionWithFeeItems(billItemCollection, feeItems)
            insertResult.value = false
        }
    }

    var deleteResult = MutableStateFlow<Boolean>(false)
    var deleteProcess = MutableStateFlow<Boolean>(false)
    fun deleteBillItemCol(itemCollection: BillItemCollectionWithBillItems) {
        deleteProcess.value = true
        viewModelScope.launch {
            deleteResult.value = try {
                itemCollection.itemList.forEach {
                    deleteBillItem(it)
                }
                billingRepository.deleteBillItemCol(itemCollection.itemCollection)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            deleteProcess.value = false
        }
    }

    private fun deleteBillItem(item: BillItem) {
        viewModelScope.launch {
            deleteResult.value = billingRepository.deleteBillItem(item)
        }
    }
}