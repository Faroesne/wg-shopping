package com.example.communityshopping.purchasing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.communityshopping.R
import com.example.communityshopping.databinding.ActivityPurchasingBinding
import com.example.communityshopping.mainActivity.archive.ArchiveDetailAdapter

class PurchasingActivity : AppCompatActivity() {

    private lateinit var singleButton: Button
    private lateinit var totalButton: Button
    lateinit var confirmBtn: Button
    private lateinit var binding: ActivityPurchasingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        singleButton = findViewById(R.id.singleBtn2)
        singleButton.setOnClickListener { changeToSingle() }
        totalButton = findViewById(R.id.totalBtn2)
        totalButton.setOnClickListener { changeToTotal() }
        confirmBtn = findViewById(R.id.confirmBuyBtn2)
        confirmBtn.setOnClickListener { pickCameraOrGallery() }
        val namesList = intent.getSerializableExtra("names") as ArrayList<String>
       /* val nameView: TextView = view.findViewById(R.id.pricingName)
        val iterator = namesList.iterator()
        for (item in iterator) {
            nameView.text = item
            view.apply {
                if (parent != null) {
                    (parent as ViewGroup).removeView(this)

                }
            }
        }
        */
        binding.scroll.adapter = PurchasingAdapter(this, namesList)
    }

    private fun changeToTotal() {

        val card = binding.scroll.findViewById<EditText>(R.id.itemPrice)
        card.visibility = View.INVISIBLE
        binding.totalPrice.visibility = View.VISIBLE
        totalButton.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        singleButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)


    }


    private fun pickCameraOrGallery() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 123)
        }
    }


    private fun changeToSingle() {

        val card = binding.scroll.findViewById<EditText>(R.id.itemPrice)
        card.visibility = View.VISIBLE
        binding.totalPrice.visibility = View.INVISIBLE
        singleButton.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        totalButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            var bmp: Bitmap = data?.extras?.get("data") as Bitmap
        }
    }

}