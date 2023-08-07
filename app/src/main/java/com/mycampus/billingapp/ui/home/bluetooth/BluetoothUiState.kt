package com.mycampus.billingapp.ui.home.bluetooth

import com.mycampus.billingapp.domain.bluetooth.BluetoothDevice

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
)