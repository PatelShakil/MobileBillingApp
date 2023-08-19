package com.mycampus.billingapp.ui.backuprestore

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.billingapp.data.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@HiltViewModel
class BackupRestoreViewModel @Inject constructor(private val appDatabase: AppDatabase) :
ViewModel(){


    fun backupDatabase(context : Context,backupFile: File) {
        viewModelScope.launch {
            appDatabase.backupDatabase(context,backupFile)
        }
    }
    fun restoreDatabaseFromUri(context:Context,uri: Uri) {
        viewModelScope.launch {
            val inputStream = context.contentResolver.openInputStream(uri)

            inputStream?.use {
                val backupFile = File(context.cacheDir, "restored_database.db")
                val outputStream = FileOutputStream(backupFile)
                inputStream.copyTo(outputStream)
                outputStream.close()

                appDatabase.restoreDatabase(context,backupFile)

                // Optionally, you can delete the temporary backup file after restore
                backupFile.delete()
            }
        }
    }

    fun restoreDatabase(context:Context,backupFile: File) {
        viewModelScope.launch {
            appDatabase.restoreDatabase(context,backupFile)
        }
    }



}