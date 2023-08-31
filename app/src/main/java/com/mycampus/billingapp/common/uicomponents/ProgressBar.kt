package com.mycampus.billingapp.common.uicomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mycampus.billingapp.common.MainColor

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
@Composable
fun ProgressBarCus(onDismiss:()->Unit,progress : Float) {
    Dialog(onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(shape = RoundedCornerShape(20.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = MainColor, modifier = Modifier.padding(30.dp),
                    progress = progress
                )
                Text("${progress * 100f}%", textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}