package com.mycampus.mobilebilling.ui.backuprestore

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mycampus.mobilebilling.common.Utils.Companion.generateExcelSheet
import com.mycampus.mobilebilling.data.models.BillItemCollectionExcel
import com.mycampus.mobilebilling.data.room.AppDatabase
import com.mycampus.mobilebilling.data.room.RestoreProgressListener
import com.mycampus.mobilebilling.data.room.RoomDao
import com.mycampus.mobilebilling.data.room.entities.BillItem
import com.mycampus.mobilebilling.data.room.entities.BillItemCollection
import com.mycampus.mobilebilling.data.room.entities.CustomerItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BackupRestoreViewModel @Inject constructor(private val appDatabase: AppDatabase,private val dao:RoomDao) :
ViewModel(){



    val billitemsCol = dao.getAllBillItemsCol().asLiveData()
    val billitems = dao.getAllBillItems().asLiveData()
    val customers = dao.getAllCustomers().asLiveData()
    val billitemcollection = dao.getAllItemCollectionsWithBillItems().asLiveData()
    fun backupDatabase(billitemCols : List<BillItemCollection>, billitems:List<BillItem>, customers : List<CustomerItem>, context : Context) {
        viewModelScope.launch {
            appDatabase.backupDatabase(billitemCols,billitems,customers,context)
        }
    }

    fun restoreDatabase(context:Context,progressListener: RestoreProgressListener) {
        viewModelScope.launch {
            appDatabase.restoreDatabase(progressListener)
        }
    }

    val downloadExcelResult = MutableStateFlow<Boolean>(false)

    fun generateExcel(excelDataList: List<BillItemCollectionExcel>) = viewModelScope.launch{
        downloadExcelResult.value = true
        generateExcelSheet(excelDataList).apply {
            downloadExcelResult.value = false

        }
    }


}