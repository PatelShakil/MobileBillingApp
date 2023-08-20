package com.mycampus.billingapp.ui.backuprestore

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mycampus.billingapp.data.room.AppDatabase
import com.mycampus.billingapp.data.room.RoomDao
import com.mycampus.billingapp.data.room.entities.BillItem
import com.mycampus.billingapp.data.room.entities.BillItemCollection
import com.mycampus.billingapp.data.room.entities.CustomerItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BackupRestoreViewModel @Inject constructor(private val appDatabase: AppDatabase,private val dao:RoomDao) :
ViewModel(){



    val billitemsCol = dao.getAllBillItemsCol().asLiveData()
    val billitems = dao.getAllBillItems().asLiveData()
    val customers = dao.getAllCustomers().asLiveData()
    fun backupDatabase(billitemCols : List<BillItemCollection>, billitems:List<BillItem>, customers : List<CustomerItem>, context : Context) {
        viewModelScope.launch {
            appDatabase.backupDatabase(billitemCols,billitems,customers,context)
        }
    }

    fun restoreDatabase(context:Context) {
        viewModelScope.launch {
            appDatabase.restoreDatabase(context)
        }
    }



}