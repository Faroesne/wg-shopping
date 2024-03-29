package com.example.communityshopping.purchasing

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.iterator
import com.example.communityshopping.CommunityShoppingApplication
import com.example.communityshopping.R
import com.example.communityshopping.database.ShoppingListDB
import com.example.communityshopping.database.models.Item
import com.example.communityshopping.databinding.ActivityPurchasingBinding
import com.example.communityshopping.mainActivity.MainActivity
import io.github.muddz.styleabletoast.StyleableToast
import java.io.ByteArrayOutputStream


class PurchasingActivity : AppCompatActivity() {

    private lateinit var singleButton: Button
    private lateinit var totalButton: Button
    lateinit var confirmBtn: Button
    private lateinit var binding: ActivityPurchasingBinding
    private var idList = arrayListOf<String>()
    var scroll: LinearLayout? = null
    private var itemList = arrayListOf<Item>()
    private var isTotal: Boolean = true
    private lateinit var global: CommunityShoppingApplication.Global

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        global = (application as CommunityShoppingApplication).global
        binding = ActivityPurchasingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        singleButton = findViewById(R.id.singleBtn2)
        singleButton.setOnClickListener { changeToSingle() }
        totalButton = findViewById(R.id.totalBtn2)
        scroll = findViewById(R.id.scroll)
        totalButton.setOnClickListener { changeToTotal() }
        confirmBtn = findViewById(R.id.confirmBuyBtn2)
        confirmBtn.setOnClickListener {
            if (isTotal) {
                if (binding.totalPrice.text.toString() == "") {
                    StyleableToast.makeText(
                        this,
                        this.getString(R.string.no_price),
                        R.style.toastStyle
                    ).show()
                } else {
                    pickCameraOrGallery()
                }
            } else {
                var allowNext = false
                for (item in itemList) {
                    var price = item.view.findViewById<EditText>(R.id.itemPrice).text
                    if (price.toString() != "") {
                        allowNext = true
                    } else {
                        allowNext = false
                        break
                    }
                }
                if (allowNext) {
                    pickCameraOrGallery()
                } else {
                    StyleableToast.makeText(
                        this,
                        this.getString(R.string.no_price),
                        R.style.toastStyle
                    ).show()
                }
            }
        }
        idList = intent.getSerializableExtra("ids") as ArrayList<String>
        addCards(idList)
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
        window.setNavigationBarColor(Color.parseColor("#0C0B0B"))
    }

    /**
     * This method changes the UI Interface and the visual look of the screen if the user paid in total
     */

    private fun changeToTotal() {
        val iterator = binding.scroll.iterator()
        for (item in iterator) {
            val cardPrice = item.findViewById<EditText>(R.id.itemPrice)
            if (cardPrice.visibility == View.VISIBLE) {
                cardPrice.visibility = View.INVISIBLE
            }
            val cardEuro = item.findViewById<TextView>(R.id.euroSignItem)
            if (cardEuro.visibility == View.VISIBLE) {
                cardEuro.visibility = View.INVISIBLE
            }
        }
        binding.totalPrice.visibility = View.VISIBLE
        binding.totalPrice.isEnabled = true
        if (binding.totalPrice.text.toString() == "0.0") {
            binding.totalPrice.setText("")
        }
        totalButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0C0B0B"))
        totalButton.setTextColor(Color.WHITE)
        singleButton.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        singleButton.setTextColor(Color.parseColor("#0C0B0B"))
        isTotal = true
    }

    /**
     * This method takes all cards from the DB and adds them to the linearlayout
     */

    private fun addCards(list: ArrayList<String>) {
        val db = ShoppingListDB(this, null)
        for (itemId in list) {
            val cursor = db.getShoppingListDataNameByID(itemId)
            val view: View = layoutInflater.inflate(R.layout.pricing_card, null)
            val textView: TextView = view.findViewById(R.id.pricingName)
            if (cursor != null) {
                cursor.moveToNext()
                textView.text =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_NAME))
            }
            val item = Item(view, itemId)
            itemList.add(item)
            scroll!!.addView(view)
        }
    }

    /**
     * This method checks/takes camera permissions and captures an image with the device
     */

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

    /**
     * This method changes the UI Interface and the visual look of the screen if the user pays single items
     */

    private fun changeToSingle() {
        val iterator = binding.scroll.iterator()
        for (item in iterator) {
            val card = item.findViewById<EditText>(R.id.itemPrice)
            if (card.visibility == View.INVISIBLE) {
                card.visibility = View.VISIBLE
            }
            val cardEuro = item.findViewById<TextView>(R.id.euroSignItem)
            if (cardEuro.visibility == View.INVISIBLE) {
                cardEuro.visibility = View.VISIBLE
            }
        }
        updateText()
        binding.totalPrice.isEnabled = false
        singleButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#0C0B0B"))
        singleButton.setTextColor(Color.WHITE)
        totalButton.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        totalButton.setTextColor(Color.parseColor("#0C0B0B"))
        isTotal = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            val bmp: Bitmap = data?.extras?.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val imageAsByteArray = stream.toByteArray()
            val db = ShoppingListDB(this, null)
            val sharedPref = this.getSharedPreferences(
                getString(R.string.app_preferences), Context.MODE_PRIVATE
            )
            val username = sharedPref.getString(getString(R.string.user_name), "unknown")
            val archiveId =
                db.addArchiveListItem(
                    binding.totalPrice.text.toString().toDouble(),
                    username!!,
                    imageAsByteArray
                )
            for (item in itemList) {
                db.deleteShoppingListItem(item.id)
                var price = item.view.findViewById<EditText>(R.id.itemPrice).text
                if (isTotal) {
                    db.addArchiveItem(null, item.id, archiveId)
                } else {
                    db.addArchiveItem(
                        price.toString().toDouble(),
                        item.id,
                        archiveId
                    )
                }
            }
            val i = Intent(this, MainActivity::class.java)
            global.resend = true
            startActivity(i)
        }
    }

    /**
     * This methods keeps the total value of the cards updated with every change
     */

    private fun updateText() {
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