package com.mycampus.mobilebilling.ui.home.bluetooth

import com.mycampus.mobilebilling.domain.bluetooth.BluetoothDevice

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
)