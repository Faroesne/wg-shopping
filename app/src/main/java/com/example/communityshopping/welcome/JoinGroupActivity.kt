package com.example.communityshopping.welcome

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.communityshopping.mainActivity.MainActivity
import com.example.communityshopping.R
import com.theartofdev.edmodo.cropper.CropImage

class JoinGroupActivity : AppCompatActivity() {

    lateinit var joinBtn: Button
    var totalLayout: LinearLayout? = null
    lateinit var view: View

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_group)
        joinBtn = findViewById(R.id.gruppeBeitreten2)
        joinBtn.setOnClickListener { joinGroup() }
        totalLayout = findViewById(R.id.containerList)
        view = layoutInflater.inflate(R.layout.group_card, null)
        totalLayout!!.addView(view)
    }

    private fun joinGroup() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}