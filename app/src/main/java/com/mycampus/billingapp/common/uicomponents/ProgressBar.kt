package com.mycampus.billingapp.common.uicomponents

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mycampus.billingapp.ui.home.MainColor

@Composable
fun ProgressBarCus(onDismiss:()->Unit) {
    Dialog(onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(shape = RoundedCornerShape(20.dp)) {
            CircularProgressIndicator(color = MainColor,modifier = Modifier.padding(30.dp))

        }
    }
}