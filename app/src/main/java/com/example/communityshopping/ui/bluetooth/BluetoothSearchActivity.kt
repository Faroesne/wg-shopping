package com.example.communityshopping.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.communityshopping.R

class BluetoothSearchActivity : AppCompatActivity() {

    private var REQUEST_BT_ENABLE = 1

    private lateinit var groupBluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothEnableIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_search)

        groupBluetoothAdapter = initBluetoothAdapter()

        enableBluetoothPermissions()

        bluetoothEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

        startActivityForResult(bluetoothEnableIntent, REQUEST_BT_ENABLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BT_ENABLE && resultCode == RESULT_OK) {
            Toast.makeText(applicationContext, "Bluetooth aktiviert", Toast.LENGTH_LONG).show()
        }
    }

    private fun initBluetoothAdapter(): BluetoothAdapter {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val groupBluetoothManager = getSystemService(BluetoothManager::class.java)
            return groupBluetoothManager.adapter
        } else {
            return BluetoothAdapter.getDefaultAdapter()
        }
    }

    private fun enableBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
        }
    }
}