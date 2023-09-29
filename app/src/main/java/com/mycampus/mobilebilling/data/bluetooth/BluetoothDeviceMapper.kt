package com.mycampus.mobilebilling.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.mycampus.mobilebilling.domain.bluetooth.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}