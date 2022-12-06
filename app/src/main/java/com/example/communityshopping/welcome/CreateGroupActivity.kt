package com.example.communityshopping.welcome

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.communityshopping.R
import com.example.communityshopping.communication.BluetoothHelper
import com.example.communityshopping.communication.ServerSocketBluetooth
import com.example.communityshopping.mainActivity.MainActivity
import java.util.*

class CreateGroupActivity : AppCompatActivity() {


    lateinit var createButton: Button


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        createButton = findViewById(R.id.gruppeErstellen)
        createButton.setOnClickListener {
            createGroup()
        }

        initSrvSocket()
    }

    private fun initSrvSocket(){
        val btHelper = BluetoothHelper(this)
        val srvSocket = ServerSocketBluetooth(btHelper.groupBluetoothAdapter)
        srvSocket.start()
    }
    private fun createGroup() {
        saveBluetoothStatus()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun saveBluetoothStatus() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.app_preferences), Context.MODE_PRIVATE
        )
        with(sharedPref.edit()) {
            putInt(getString(R.string.bluetooth_setup_status), 1)
            Log.i("sharedPref", "UserName gespeichert.")
            apply()
        }
    }
}