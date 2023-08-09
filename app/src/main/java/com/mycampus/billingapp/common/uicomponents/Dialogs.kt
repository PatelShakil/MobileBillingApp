package com.mycampus.billingapp.common.uicomponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

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