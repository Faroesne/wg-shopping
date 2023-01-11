package com.example.communityshopping.mainActivity.archive

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getDoubleOrNull
import com.example.communityshopping.R
import com.example.communityshopping.database.ShoppingListDB
import com.example.communityshopping.databinding.ActivityArchiveBinding
import com.example.communityshopping.mainActivity.archive.models.Archive
import java.util.*


class ArchiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArchiveBinding
    lateinit var receiptButton: Button
    lateinit var closeButton: Button
    lateinit var image: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArchiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiptButton = findViewById(R.id.findReceipt)
        closeButton = findViewById(R.id.closeArchive)
        closeButton.setOnClickListener {
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val archiveArrayList = intent.getSerializableExtra("archiveArrayList") as ArrayList<Archive>
        val position = intent.getIntExtra("position", 0)

        setTitle(archiveArrayList[position].title)
        binding.infoArchive.text = archiveArrayList[position].info

        val db = ShoppingListDB(this, null)
        val sharedPref = this.getSharedPreferences(
            getString(R.string.app_preferences), Context.MODE_PRIVATE
        )
        val username = sharedPref.getString(getString(R.string.user_name), "unknown")
        val dbUsername = archiveArrayList[position].username
        if (dbUsername != username) {
            receiptButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            receiptButton.isEnabled = false
        }
        receiptButton.setOnClickListener {
            setContentView(R.layout.receipt_image)
            val receiptImage: ImageView = findViewById(R.id.receiptImage)
            image = archiveArrayList[position].bmp
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            receiptImage.setImageBitmap(bitmap)

        }

        val archiveItem = db.getArchiveItemByID(archiveArrayList[position].index)
        var archiveList = ArrayList<archiveItem>()

        if (archiveItem!!.count >= 1) {
            while (archiveItem.moveToNext()) {
                archiveList.add(
                    archiveItem(
                        archiveItem.getString
                            (
                            archiveItem.getColumnIndexOrThrow
                                (ShoppingListDB.COLUMN_ITEM_NAME)
                        ),
                        archiveItem.getDoubleOrNull
                            (
                            archiveItem.getColumnIndexOrThrow
                                (ShoppingListDB.COLUMN_ITEM_PRICE)
                        )
                    )
                )
            }
            archiveItem.close()
        }
        binding.archiveFullPrice.text =
            "%,.2f".format(Locale.GERMAN, archiveArrayList[position].fullPrice) + "€"

        if (archiveArrayList != null) {
            binding.archiveArticles.adapter =
                ArchiveDetailAdapter(this, archiveList)
        }

        window.setNavigationBarColor(Color.parseColor("#0C0B0B"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}