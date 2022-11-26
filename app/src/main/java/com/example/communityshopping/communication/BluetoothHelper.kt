package com.example.communityshopping.communication

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult

class BluetoothHelper(activity: Activity) {

    private val mActivity: Activity

    private var REQUEST_BT_ENABLE = 1
    private var REQUEST_BT_PERMISSION = 2

    private lateinit var groupBluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothEnableIntent: Intent

    init {
        mActivity = activity;
        groupBluetoothAdapter = initBluetoothAdapter()
        enableBluetoothPermissions()
        enableBluetoothFunction()
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
                arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),
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

}