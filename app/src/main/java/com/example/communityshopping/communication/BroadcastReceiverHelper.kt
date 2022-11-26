package com.example.communityshopping.communication

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Message

class BroadcastReceiverHelper : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val msg: Message = Message.obtain()
        val action = intent.action
        if (BluetoothDevice.ACTION_FOUND == action) {
            //Found, add to a device list
        }
    }
}