package com.example.communityshopping.communication

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class BroadcastReceiverHelper : BroadcastReceiver() {
    val deviceList = ArrayList<String>()

    init {
        deviceList.add("TestDevice")
    }

    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (BluetoothDevice.ACTION_FOUND == action) {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
//TODO
            }
            var device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            deviceList.add(device!!.name)
        }
    }
}