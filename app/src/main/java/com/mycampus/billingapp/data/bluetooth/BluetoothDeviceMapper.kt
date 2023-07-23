package com.mycampus.billingapp.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.mycampus.billingapp.domain.bluetooth.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}