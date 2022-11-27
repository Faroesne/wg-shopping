package com.example.communityshopping.welcome

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.communityshopping.R
import com.example.communityshopping.communication.BluetoothHelper
import com.example.communityshopping.mainActivity.MainActivity

class JoinGroupActivity : AppCompatActivity() {

    lateinit var joinBtn: Button
    var totalLayout: LinearLayout? = null
    lateinit var view: View
    var deviceList: MutableSet<BluetoothDevice> = mutableSetOf()

    private lateinit var bluetoothHelper: BluetoothHelper

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_group)
        joinBtn = findViewById(R.id.gruppeBeitreten2)
        joinBtn.setOnClickListener { joinGroup() }
        totalLayout = findViewById(R.id.containerList)


        bluetoothHelper = BluetoothHelper(this)
        val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, intentFilter)

        bluetoothHelper.startSearching()
    }


    private fun joinGroup() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bluetoothHelper.onActivityResult(requestCode, resultCode, data)
    }

    var mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                var device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                Log.i("BluetoothReceiver", "found")
                if (ActivityCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //TODO activate Bluetooth permission
                    bluetoothHelper.enableBluetoothPermissions()
                    return
                }

                if (!deviceList.contains(device)) {
                    deviceList.add(device!!)
                    var view: View = layoutInflater.inflate(R.layout.group_card, null)
                    var textView: TextView = view.findViewById(R.id.groupName)
                    textView.text = device!!.name
                    totalLayout!!.addView(view)
                }

                //deviceList.add(device!!.name)
            } else if (action.equals(
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                )
            ) {
                // discoveryFinished
                Log.i("BluetoothReceiver", "finshed")
            } else if (action.equals(
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED
                )
            ) {
                // discoveryStarted
                Log.i("BluetoothReceiver", "started")
            }
        }
    }
}