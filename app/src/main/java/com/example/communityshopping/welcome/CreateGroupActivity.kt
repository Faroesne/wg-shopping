package com.example.communityshopping.welcome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.communityshopping.R
import com.example.communityshopping.mainActivity.MainActivity

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

}