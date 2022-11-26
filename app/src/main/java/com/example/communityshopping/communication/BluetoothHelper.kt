package com.example.communityshopping.communication

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult


class BluetoothHelper(activity: Activity) {

    private val mActivity: Activity

    private var REQUEST_BT_ENABLE = 1
    private var REQUEST_BT_PERMISSION = 2

    private lateinit var groupBluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothEnableIntent: Intent

    private val myReceiver: BroadcastReceiverHelper = BroadcastReceiverHelper()

    init {
        mActivity = activity;
        groupBluetoothAdapter = initBluetoothAdapter()
        enableBluetoothPermissions()
        enableBluetoothFunction()
    }

    fun getDeviceList(): ArrayList<String> {
        return myReceiver.deviceList
    }

    fun startSearching() {
        Log.i("Log", "in the start searching method")
        val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        mActivity.registerReceiver(myReceiver, intentFilter)
        if (ActivityCompat.checkSelfPermission(
                mActivity.applicationContext,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //TODO activate permission
        }
        groupBluetoothAdapter.startDiscovery()
    }

    private fun initBluetoothAdapter(): BluetoothAdapter {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val groupBluetoothManager = mActivity.getSystemService(BluetoothManager::class.java)
            groupBluetoothManager.adapter
        } else {
            BluetoothAdapter.getDefaultAdapter()
        }
    }

    private fun enableBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                mActivity,
                arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_BT_PERMISSION
            )
        }
    }

    private fun enableBluetoothFunction() {
        if (!groupBluetoothAdapter.isEnabled) {

            bluetoothEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            startActivityForResult(mActivity, bluetoothEnableIntent, REQUEST_BT_ENABLE, null)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_BT_ENABLE && resultCode == AppCompatActivity.RESULT_OK) {
            Toast.makeText(mActivity.applicationContext, "Bluetooth aktiviert", Toast.LENGTH_LONG)
                .show()
        }
    }
}