package com.example.communityshopping.welcome

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.communityshopping.R
import com.example.communityshopping.communication.BluetoothHelper
import com.example.communityshopping.mainActivity.MainActivity

class WelcomeActivity : AppCompatActivity() {

    lateinit var joinBtn: Button
    lateinit var createButton: Button
    lateinit var textField: TextView

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
        textField = findViewById(R.id.textFieldName)

        checkForExistingSetup()
        bluetoothHelper = BluetoothHelper(this)

        window.setNavigationBarColor(Color.parseColor("#0C0B0B"))
    }


    private fun joinGroup() {
        saveUserName()
        startActivity(Intent(this, JoinGroupActivity::class.java))
    }

    private fun createGroup() {
        saveUserName()
        startActivity(Intent(this, CreateGroupActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bluetoothHelper.onActivityResult(requestCode, resultCode, data)
    }

    private fun saveUserName() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.app_preferences), Context.MODE_PRIVATE
        )
        with(sharedPref.edit()) {
            if (textField.text.isBlank()) {
                putString(getString(R.string.user_name), "Default")
            } else {
                putString(getString(R.string.user_name), textField.text.toString())
            }
            Log.i("sharedPref", "UserName gespeichert.")
            apply()
        }
    }

    private fun checkForExistingSetup() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.app_preferences), Context.MODE_PRIVATE
        )
        val bluetoothSetup = sharedPref.getInt(getString(R.string.bluetooth_setup_status), 0)
        Log.i("sharedPref", bluetoothSetup.toString())
        if (bluetoothSetup == 1) {
            Log.i("sharedPref", "Main screen loaded")
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}