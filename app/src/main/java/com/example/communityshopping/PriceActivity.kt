package com.example.communityshopping

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.communityshopping.databinding.ActivityPriceBinding
import com.theartofdev.edmodo.cropper.CropImage

class PriceActivity : AppCompatActivity() {

    var layout: LinearLayout? = null
    private lateinit var binding: ActivityPriceBinding

    lateinit var confirmBuyBtn:Button
    lateinit var totalButton: Button

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price)
        binding = ActivityPriceBinding.inflate(layoutInflater)
        layout = findViewById(R.id.priceContainerList)
        totalButton = findViewById(R.id.totalBtn)
        confirmBuyBtn = findViewById(R.id.confirmBuyBtn)
        totalButton.setOnClickListener { changeToTotal() }
        confirmBuyBtn.setOnClickListener { pickCameraOrGallery() }

    }
    private fun changeToTotal()
    {
        startActivity(Intent(this,TotalPriceActivity::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pickCameraOrGallery ()
    {
        val pick: Boolean = true
        if(pick)
        {
            if(!checkCameraPermission())
            {
                requestCameraPermission()
            }else
                pickImage()
        }else
        {
            if(!checkStoragePermission())
            {
                requestStoragePermission()
            }else
                pickImage()
        }
    }

    private fun pickImage() {
        CropImage.activity().start(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestStoragePermission() {
        requestPermissions(Array<String>(1){Manifest.permission.WRITE_EXTERNAL_STORAGE},100)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        requestPermissions(Array<String>(2){Manifest.permission.CAMERA; Manifest.permission.WRITE_EXTERNAL_STORAGE},100)
    }

    private fun checkStoragePermission(): Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermission(): Boolean {
        val firstResource: Boolean = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val secondResource: Boolean = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return firstResource && secondResource
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