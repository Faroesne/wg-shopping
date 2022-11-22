package com.example.communityshopping

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.communityshopping.databinding.ActivityArchiveBinding
import java.util.*
import kotlin.collections.ArrayList


class ArchiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArchiveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArchiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val archiveArrayList = intent.getSerializableExtra("archiveArrayList") as ArrayList<Archive>
        val position = intent.getIntExtra("position", 0)

        setTitle(archiveArrayList[position].title)
        binding.infoArchive.text = archiveArrayList[position].info

        var fullPrice = 0.0
        for (i in archiveArrayList[position].archiveItems.indices) {
            fullPrice += archiveArrayList[position].archiveItems[i].price
        }
        binding.archiveFullPrice.text = "%,.2f".format(Locale.GERMAN, fullPrice) + "€"

        if (archiveArrayList != null) {
            binding.archiveArticles.adapter = ArchiveDetailAdapter(this, archiveArrayList[position].archiveItems)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}