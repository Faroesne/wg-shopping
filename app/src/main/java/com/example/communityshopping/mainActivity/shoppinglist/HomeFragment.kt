package com.example.communityshopping.mainActivity.shoppinglist

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.communityshopping.R
import com.example.communityshopping.database.models.Item
import com.example.communityshopping.database.ShoppingListDB
import com.example.communityshopping.databinding.FragmentHomeBinding
import com.example.communityshopping.purchasing.PurchasingActivity

class HomeFragment : Fragment() {

    lateinit var add: ImageButton
    lateinit var btnDelete: ImageButton
    lateinit var btnSubmit: Button
    var itemList = arrayListOf<Item>()
    var dialog: AlertDialog? = null
    var layout: LinearLayout? = null
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        add = root.findViewById(R.id.btn_addItem)
        btnDelete = root.findViewById(R.id.btn_delete)
        btnSubmit = root.findViewById(R.id.btn_submit)
        layout = root.findViewById(R.id.containerList)
        buildDialog()
        add.setOnClickListener({ dialog!!.show() })
        btnDelete.setOnClickListener({ removeItems() })
        btnSubmit.setOnClickListener({ submitItems() })
        dbGetShoppingList()
        return root
    }

    private fun buildDialog(view: View = layoutInflater.inflate(R.layout.dialog, null)) {
        val builder = AlertDialog.Builder(view.context)
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

    private fun dbGetShoppingList() {
        val db = ShoppingListDB(this.context, null)
        val cursor = db.getAllTableData()
        if (cursor!!.count >= 1) {
            while (cursor.moveToNext()) {
                val view: View = layoutInflater.inflate(R.layout.card, null)
                val nameView: TextView = view.findViewById(R.id.name)
                nameView.text =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_NAME))
                val item = Item(
                    view,
                    cursor.getLong(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ID))
                )
                itemList.add(item)
                layout!!.addView(view)
            }
            cursor.close()
        }
    }

    private fun addCard(name: String) {
        val view: View = layoutInflater.inflate(R.layout.card, null)
        val nameView: TextView = view.findViewById(R.id.name)
        val db = ShoppingListDB(this.context, null)
        val id = db.addItem(name)
        val item = Item(view, id)
        nameView.text = name
        itemList.add(item)
        layout!!.addView(view)
        Toast.makeText(
            this.context,
            name + " wurde der Einkaufsliste hinzugefügt",
            Toast.LENGTH_LONG
        ).show()

    }

    private fun removeItems() {
        val iterator = itemList.iterator()
        for (item in iterator) {
            if (item.view.findViewById<CheckBox>(R.id.checkbox).isChecked) {
                dbDeleteItem(item.id)
                layout!!.removeView(item.view)
                iterator.remove()
            }
        }
    }

    private fun dbDeleteItem(id: Long) {
        val db = ShoppingListDB(this.context, null)
        db.deleteItem(id)
    }

    private fun submitItems() {
        startActivity(Intent(activity, PurchasingActivity::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}