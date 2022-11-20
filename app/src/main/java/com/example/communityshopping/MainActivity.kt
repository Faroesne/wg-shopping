package com.example.communityshopping

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.communityshopping.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var add: ImageButton
    lateinit var btnDelete: ImageButton
    lateinit var btnSubmit: Button
    var itemList = arrayListOf<View>()
    var dialog: AlertDialog? = null
    var layout: LinearLayout? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        add = findViewById(R.id.btn_addItem)
        btnDelete = findViewById(R.id.btn_delete)
        btnSubmit = findViewById(R.id.btn_submit)
        layout = findViewById(R.id.containerList)
        buildDialog()
        add.setOnClickListener({ dialog!!.show() })
        btnDelete.setOnClickListener({ removeItems() })
        btnSubmit.setOnClickListener({ submitItems() })
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        dbGetShoppingList()

    }

    private fun buildDialog() {
        val builder = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.dialog, null)
        val name = view.findViewById<EditText>(R.id.nameEdit)
        builder.setView(view)
        builder.setTitle("Enter article")
            .setPositiveButton(
                "OK"
            ) { dialog, which -> addCard(name.text.toString()) }
            .setNegativeButton(
                "Cancel"
            ) { dialog, which -> }
        dialog = builder.create()
    }
    private fun dbGetShoppingList(){
        val db = DatabaseHelper(this, null)
        val cursor = db.getAllTableData(Table.TABLE_SHOPPING_LIST)
        if(cursor!!.getCount() >= 1) {
            while (cursor.moveToNext()) {
                val view: View = layoutInflater.inflate(R.layout.card, null)
                val nameView: TextView = view.findViewById(R.id.name)
                nameView.text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_NAME))
                itemList.add(view)
                layout!!.addView(view)
            }
        }
    }
    private fun addCard(name: String) {
        val view: View = layoutInflater.inflate(R.layout.card, null)
        val nameView: TextView = view.findViewById(R.id.name)
        val db = DatabaseHelper(this, null)
        db.addItem(name)
        nameView.text = name
        itemList.add(view)
        layout!!.addView(view)
        Toast.makeText(this, name + " wurde der Einkaufsliste hinzugefügt", Toast.LENGTH_LONG).show()

    }

    private fun removeItems(){
        val iterator = itemList.iterator()
        for(item in iterator){
            if(item.findViewById<CheckBox>(R.id.checkbox).isChecked){
                layout!!.removeView(item)
                iterator.remove()
            }
        }
    }
    private fun submitItems(){
        startActivity(Intent(this, TotalPriceActivity::class.java))
    }
}