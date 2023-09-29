package com.mycampus.mobilebilling.common.uicomponents

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ErrorMessage(msg : String) {
    Text(
        msg, modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center
    )
}