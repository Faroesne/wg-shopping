package com.example.communityshopping.purchasing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.communityshopping.R
import com.theartofdev.edmodo.cropper.CropImage

class PurchasingActivity : AppCompatActivity() {

    private lateinit var singleButton: Button
    private lateinit var totalButton: Button
    lateinit var confirmBtn: Button
    var totalLayout: LinearLayout? = null
    lateinit var view: View

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchasing)
        singleButton = findViewById(R.id.singleBtn2)
        singleButton.setOnClickListener { changeToSingle() }
        totalButton = findViewById(R.id.totalBtn2)
        totalButton.setOnClickListener { changeToTotal() }
        confirmBtn = findViewById(R.id.confirmBuyBtn2)
        confirmBtn.setOnClickListener { pickCameraOrGallery() }
        totalLayout = findViewById(R.id.totalLayout)
        view = layoutInflater.inflate(R.layout.pricing_card, null)
        totalLayout!!.addView(view)

    }

    private fun changeToTotal() {
        val card = view.findViewById<EditText>(R.id.itemPrice)
        val item2 = findViewById<EditText>(R.id.totalPrice)
        card.visibility = View.INVISIBLE
        item2.visibility = View.VISIBLE
        totalButton.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        singleButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pickCameraOrGallery() {
        val pick: Boolean = true
        if (pick) {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else
                pickImage()
        } else {
            if (!checkStoragePermission()) {
                requestStoragePermission()
            } else
                pickImage()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestStoragePermission() {
        requestPermissions(Array<String>(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 100)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickImage() {
        CropImage.activity().start(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        requestPermissions(
            Array<String>(2) { Manifest.permission.CAMERA; Manifest.permission.WRITE_EXTERNAL_STORAGE },
            100
        )
    }

    private fun checkCameraPermission(): Boolean {
        val firstResource: Boolean = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val secondResource: Boolean = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return firstResource && secondResource
    }

    private fun changeToSingle() {
        val card = view.findViewById<EditText>(R.id.itemPrice)
        val item2 = findViewById<EditText>(R.id.totalPrice)
        card.visibility = View.VISIBLE
        item2.visibility = View.INVISIBLE
        singleButton.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        totalButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
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