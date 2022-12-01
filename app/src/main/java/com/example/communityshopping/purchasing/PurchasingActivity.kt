package com.example.communityshopping.purchasing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.example.communityshopping.R
import com.example.communityshopping.database.ShoppingListDB
import com.example.communityshopping.databinding.ActivityPurchasingBinding
import com.example.communityshopping.mainActivity.MainActivity
import com.example.communityshopping.mainActivity.archive.ArchiveDetailAdapter
import com.example.communityshopping.mainActivity.shoppinglist.HomeFragment
import java.util.Objects
import kotlin.math.log

class PurchasingActivity : AppCompatActivity() {

    private lateinit var singleButton: Button
    private lateinit var totalButton: Button
    lateinit var confirmBtn: Button
    private lateinit var binding: ActivityPurchasingBinding
    private var nameList = arrayListOf<Int>()
    var scroll: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        singleButton = findViewById(R.id.singleBtn2)
        singleButton.setOnClickListener { changeToSingle() }
        totalButton = findViewById(R.id.totalBtn2)
        scroll = findViewById(R.id.scroll)
        totalButton.setOnClickListener { changeToTotal() }
        confirmBtn = findViewById(R.id.confirmBuyBtn2)
        confirmBtn.setOnClickListener { pickCameraOrGallery() }
        nameList = intent.getSerializableExtra("ids") as ArrayList<Int>
        addCards(nameList)
        for (item in binding.scroll) {
            val firstCard = item.findViewById<EditText>(R.id.itemPrice)
            firstCard.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateText()
                }
            })
        }
    }

    private fun changeToTotal() {

        val iterator = binding.scroll.iterator()
        for (item in iterator) {
            val card = item.findViewById<EditText>(R.id.itemPrice)
            if (card.visibility == View.VISIBLE) {
                card.visibility = View.INVISIBLE
            }
        }
        binding.totalPrice.visibility = View.VISIBLE
        binding.totalPrice.isEnabled = true
        totalButton.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        singleButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

    }

    private fun addCards(list: ArrayList<Int>) {
        val db = ShoppingListDB(this, null)
        for (item in list) {
            val cursor = db.getShoppingListDataByID(item)
            val view: View = layoutInflater.inflate(R.layout.pricing_card, null)
            val textView: TextView = view.findViewById(R.id.pricingName)
            if (cursor != null) {
                cursor.moveToNext()
                textView.text =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_NAME))
            }
            scroll!!.addView(view)
        }
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
        val iterator = binding.scroll.iterator()
        for (item in iterator) {
            val card = item.findViewById<EditText>(R.id.itemPrice)
            if (card.visibility == View.INVISIBLE) {
                card.visibility = View.VISIBLE
            }
        }
        updateText()
        binding.totalPrice.isEnabled = false
        singleButton.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        totalButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            //var bmp: Bitmap = data?.extras?.get("data") as Bitmap
            val db = ShoppingListDB(this, null)
            for (item in nameList) {
                db.deleteShoppingListItem(item.toLong())
            }
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }

    private fun updateText()
    {
        val totalPriceArrayList = arrayListOf<Float>()
        val iterator = binding.scroll.iterator()
        for (item in iterator) {
            val card = item.findViewById<EditText>(R.id.itemPrice)
            if (card.text.isBlank() || card.text.isEmpty()) {
                totalPriceArrayList.add(0f)
            } else {
                totalPriceArrayList.add(card.text.toString().toFloat())
            }
        }
        var totalPrice = 0f
        val numberIterator = totalPriceArrayList.iterator()
        for (item in numberIterator) {
            totalPrice += item
        }
        binding.totalPrice.setText(totalPrice.toString())
    }

}