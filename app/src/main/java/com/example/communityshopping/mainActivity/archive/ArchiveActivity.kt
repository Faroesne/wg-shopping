package com.example.communityshopping.mainActivity.archive

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArchiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiptButton = findViewById(R.id.findReceipt)
        receiptButton.setOnClickListener {
            Toast.makeText(this, "Quittung", Toast.LENGTH_SHORT).show()
        }
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
        val archiveItem = db.getArchiveItemData(archiveArrayList[position].index)
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