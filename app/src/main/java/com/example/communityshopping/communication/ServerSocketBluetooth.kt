package com.example.communityshopping.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class ServerSocketBluetooth(bluetoothAdapter: BluetoothAdapter) : Thread() {

    private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("WGShopping", UUID.fromString("1240bfb6-6fc4-11ed-a1eb-0242ac120002"))
    }
    init {
        Log.i("SvrSct","Server socket created")
    }

    override fun run() {
        // Keep listening until exception occurs or a socket is returned.
        var shouldLoop = true
        while (shouldLoop) {
            val socket: BluetoothSocket? = try {
                mmServerSocket?.accept()
            } catch (e: IOException) {
                Log.e("SrvSct", "Socket's accept() method failed", e)
                shouldLoop = false
                null
            }
            Log.i("SvrSct","Client accepted")
            socket?.also {
                //manageMyConnectedSocket(it)
                mmServerSocket?.close()
                shouldLoop = false
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    fun cancel() {
        try {
            mmServerSocket?.close()
        } catch (e: IOException) {
            Log.e("SrvSct", "Could not close the connect socket", e)
        }
    }
}