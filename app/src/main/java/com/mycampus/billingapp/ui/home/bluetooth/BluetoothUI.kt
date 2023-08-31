package com.mycampus.billingapp.ui.home.bluetooth

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mycampus.billingapp.domain.bluetooth.BluetoothDevice

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Paired Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
        items(pairedDevices) { device ->
            BTDeviceItem(device = device, onClick = onClick)
        }

        item {
            Text(
                text = "Scanned Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
        items(scannedDevices) { device ->
            BTDeviceItem(device = device, onClick = onClick)
        }
    }
}

@Composable
fun BTDeviceItem(device: BluetoothDevice, onClick: (BluetoothDevice) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 5.dp)
        .clickable { onClick(device) }) {
        Text(
            text = device.name ?: "(No name)",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp, horizontal = 5.dp)
                .padding(start = 10.dp),
            style = MaterialTheme.typography.titleSmall
        )
    }

}