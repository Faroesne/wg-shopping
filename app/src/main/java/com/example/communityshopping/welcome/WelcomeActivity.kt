package com.example.communityshopping.welcome

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.communityshopping.R
import com.example.communityshopping.communication.BluetoothHelper

class WelcomeActivity : AppCompatActivity() {


    lateinit var joinBtn: Button
    lateinit var createButton: Button

    private var REQUEST_BT_ENABLE = 1
    private var REQUEST_BT_PERMISSION = 2

    private lateinit var bluetoothHelper: BluetoothHelper

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        joinBtn = findViewById(R.id.gruppeBeitreten)
        createButton = findViewById(R.id.gruppeErstellen)
        joinBtn.setOnClickListener { joinGroup() }
        createButton.setOnClickListener { createGroup() }

        bluetoothHelper = BluetoothHelper(this)
        this.title = "Willkommen"
    }

    private fun joinGroup() {
        startActivity(Intent(this, JoinGroupActivity::class.java))
    }

    private fun createGroup() {
        startActivity(Intent(this, CreateGroupActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BT_ENABLE && resultCode == RESULT_OK) {
            Toast.makeText(applicationContext, "Bluetooth aktiviert", Toast.LENGTH_LONG).show()
        }
    }
}