package com.example.communityshopping.welcome

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.communityshopping.R
import com.example.communityshopping.communication.BluetoothHelper
import com.example.communityshopping.mainActivity.MainActivity
import java.io.IOException
import java.util.*

class CreateGroupActivity : AppCompatActivity() {


    lateinit var createButton: Button

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        createButton = findViewById(R.id.gruppeErstellen)
        createButton.setOnClickListener { createGroup() }
    }

    private fun createGroup() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    @SuppressLint("MissingPermission")
    private inner class AcceptThread(activity: Activity) : Thread() {

        var bluetoothHelper = BluetoothHelper(activity)

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothHelper.groupBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("WGShopping", UUID.fromString("1240bfb6-6fc4-11ed-a1eb-0242ac120002"))
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e("ServerSocket", "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
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
                Log.e("ServerSocket", "Could not close the connect socket", e)
            }
        }
    }

}