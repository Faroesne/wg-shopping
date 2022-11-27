package com.example.communityshopping.communication

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

class BroadcastReceiverHelper : BroadcastReceiver() {
    val deviceList = ArrayList<String>()

    init {
        deviceList.add("TestDevice")
    }

    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (BluetoothDevice.ACTION_FOUND == action) {
            var device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            Log.i("blub","found")
            //deviceList.add(device!!.name)
        } else if (action.equals(
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            // discoveryFinished
            Log.i("blub","finshed")
        } else if (action.equals(
                BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
            // discoveryStarted
            Log.i("blub","started")
        }
    }
}