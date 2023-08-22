package com.mycampus.billingapp.common.uicomponents

import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mycampus.billingapp.common.Utils
import java.io.File

@Composable
fun ConfirmationDialog(onDismiss:()->Unit,onPrint:()->Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        text = {},
    title = {Text("Bill Collected Successfully",style = MaterialTheme.typography.titleMedium)},
    confirmButton = {
        Button(onClick = onPrint){
            Text("Print")
        }
    },
    dismissButton = {
        Button(onClick = onDismiss) {
            Text("Close")
        }
    })
}

@Composable
fun RestoreConfirmationDialog(onDismiss:()->Unit, onConfirm:()->Unit) {
    val backupDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/myCampus/Backup")
    if(!backupDir.exists())
        backupDir.mkdirs()
    var billitemfile =  File(backupDir.absolutePath + "/billitems.backup")
    var billitemcolsfile =  File(backupDir.absolutePath + "/billitemsCol.backup")
    var customerfile =  File(backupDir.absolutePath + "/customers.backup")
    AlertDialog(onDismissRequest = onDismiss,
        text = {
            Column {
                TextItemRow(file = billitemcolsfile)
                TextItemRow(file = billitemfile)
                TextItemRow(file = customerfile)
            }

        },
    title = {            Text("Restore Files")

    },
    confirmButton = {
        if(billitemcolsfile.exists() || billitemfile.exists() || customerfile.exists()) {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        }
    },
    dismissButton = {
        Button(onClick = onDismiss) {
            Text("No")
        }
    })
}

@Composable
fun TextItemRow(file : File) {
    Row {
        Text((if (file.exists()) file.name else "") + " : ",style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(.5f),
            softWrap = true)
        Text(if (file.exists()) Utils.convertLongToDate(file.lastModified(),"dd-MM-yyyy hh:mma") else "",style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(.5f),
            softWrap = true)
    }
}