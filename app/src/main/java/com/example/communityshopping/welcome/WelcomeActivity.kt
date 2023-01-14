package com.example.communityshopping.welcome

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.communityshopping.R
import com.example.communityshopping.database.ShoppingListDB
import com.example.communityshopping.mainActivity.MainActivity

class WelcomeActivity : AppCompatActivity() {

    lateinit var joinBtn: Button
    lateinit var createButton: Button
    lateinit var textField: TextView
    private var REQUEST_PERMISSIONS = 101

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = getString(R.string.welcome)

        setContentView(R.layout.activity_welcome)
        joinBtn = findViewById(R.id.gruppeBeitreten)
        createButton = findViewById(R.id.gruppeErstellen)
        joinBtn.setOnClickListener { joinGroup() }
        createButton.setOnClickListener { createGroup() }
        textField = findViewById(R.id.textFieldName)

        window.setNavigationBarColor(Color.parseColor("#0C0B0B"))

        checkPermissions()
        checkForExistingSetup()
    }

    /**
     * This method checks if the devices has all the required permissions
     */

    private fun checkPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val neededPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (neededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                neededPermissions.toTypedArray(),
                REQUEST_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions added
            } else {
                // Permission failed
            }
        }
    }

    /**
     * This method switches to the group join screen with the saved user's name
     */

    private fun joinGroup() {
        saveUserName()
        startActivity(Intent(this, JoinGroupActivity::class.java))
    }

    /**
     * This method switches to the group create screen with the saved user's name
     */

    private fun createGroup() {
        saveUserName()
        startActivity(Intent(this, CreateGroupActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * This method saves the username that got typed in
     */

    private fun saveUserName() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.app_preferences), Context.MODE_PRIVATE
        )
        var userName = "Default"
        with(sharedPref.edit()) {
            if (textField.text.isBlank()) {
                putString(getString(R.string.user_name), "Default")
            } else {
                userName = textField.text.toString()
                putString(getString(R.string.user_name), userName)
            }
            val db = ShoppingListDB(applicationContext, null)
            db.addUser(userName)
            Log.i("sharedPref", "UserName gespeichert.")
            apply()
        }
    }

    /**
     * This method checks if there ever was a login before and sends you to the next screen
     */

    private fun checkForExistingSetup() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.app_preferences), Context.MODE_PRIVATE
        )
        val nameSetup = sharedPref.getString(getString(R.string.user_name), null)
        Log.i("sharedPref", nameSetup.toString())
        if (nameSetup != null) {
            Log.i("sharedPref", "Main screen loaded")
            startActivity(Intent(this, JoinGroupActivity::class.java))
        }
    }
}