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

    private lateinit var bluetoothHelper: BluetoothHelper

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "Willkommen"

        setContentView(R.layout.activity_welcome)
        joinBtn = findViewById(R.id.gruppeBeitreten)
        createButton = findViewById(R.id.gruppeErstellen)
        joinBtn.setOnClickListener { joinGroup() }
        createButton.setOnClickListener { createGroup() }

        bluetoothHelper = BluetoothHelper(this)
    }

    private fun joinGroup() {
        startActivity(Intent(this, JoinGroupActivity::class.java))
    }

    private fun createGroup() {
        startActivity(Intent(this, CreateGroupActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bluetoothHelper.onActivityResult(requestCode, resultCode, data)
    }
}