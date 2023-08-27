package com.mycampus.billingapp.common.uicomponents

import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mycampus.billingapp.R
import com.mycampus.billingapp.common.Utils
import com.mycampus.billingapp.ui.home.MainColor
import java.io.File

@Composable
fun ConfirmationDialog(onDismiss: () -> Unit, onPrint: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        text = {},
        title = {
            Text(
                "Bill Collected Successfully",
                style = MaterialTheme.typography.titleMedium
            )
        },
        confirmButton = {
            Button(onClick = onPrint) {
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
fun RestoreConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val backupDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "/myCampus/Backup"
    )
    if (!backupDir.exists())
        backupDir.mkdirs()
    var billitemfile = File(backupDir.absolutePath + "/billitems.backup")
    var billitemcolsfile = File(backupDir.absolutePath + "/billitemsCol.backup")
    var customerfile = File(backupDir.absolutePath + "/customers.backup")
    AlertDialog(onDismissRequest = onDismiss,
        text = {
            Column {
                if (billitemfile.exists()) {
                    Text(
                        "last modified on ${
                            Utils.convertLongToDate(
                                billitemcolsfile.lastModified(),
                                "dd-MM-yyyy hh:mm a"
                            )
                        }"
                    )
                }
            }

        },
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_restore), "",
                        tint = MainColor
                    )
                    Text(
                        "Restore",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                    )
                }
                Spacer(Modifier.height(5.dp))
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(.5.dp), color = Color.Gray)
                Spacer(modifier = Modifier.height(5.dp))
                Text(if (billitemfile.exists()) "Backup Available" else "No Backup available, please take backup first",
                    style = MaterialTheme.typography.bodyMedium)
            }

        },
        confirmButton = {

            Button(
                onClick = onConfirm,
                enabled = billitemcolsfile.exists()
            ) {
                Text("Restore")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        })
}

@Composable
fun TextItemRow(file: File) {
    Row {
        Text(
            (if (file.exists()) file.name else "") + " : ",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(.5f),
            softWrap = true
        )
        Text(
            if (file.exists()) Utils.convertLongToDate(
                file.lastModified(),
                "dd-MM-yyyy hh:mma"
            ) else "", style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(.5f),
            softWrap = true
        )
    }
}