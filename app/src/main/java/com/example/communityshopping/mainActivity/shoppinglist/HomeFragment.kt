package com.example.communityshopping.mainActivity.shoppinglist

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.communityshopping.R
import com.example.communityshopping.database.ShoppingListDB
import com.example.communityshopping.database.models.Item
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
    var itemIdList = arrayListOf<Int>()

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
        checkIfListEmpty()
        return root
    }

    private fun buildDialog(view: View = layoutInflater.inflate(R.layout.dialog, null)) {
        val builder = AlertDialog.Builder(view.context)
        val view: View = layoutInflater.inflate(R.layout.dialog, null)
        val name = view.findViewById<EditText>(R.id.nameEdit)
        builder.setView(view)
        builder.setTitle(this.getString(R.string.enter_article))
            .setPositiveButton(
                this.getString(R.string.submit)
            ) { dialog, which -> addCard(name.text.toString()) }
            .setNegativeButton(
                this.getString(R.string.cancel)
            ) { dialog, which -> }
        dialog = builder.create()
    }

    private fun dbGetShoppingList() {

        val db = ShoppingListDB(this.context, null)
        val cursor = db.getShoppingListData()
        if (cursor!!.count >= 1) {
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_DELETED)) == 0) {
                    val view: View = layoutInflater.inflate(R.layout.card, null)
                    val nameView: TextView = view.findViewById(R.id.name)
                    nameView.text =
                        cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_NAME))
                    val item = Item(
                        view,
                        cursor.getLong(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ITEM_ID))
                    )
                    itemList.add(item)
                    layout!!.addView(view)
                }
            }
            cursor.close()
        }
    }

    private fun addCard(name: String) {
        val view: View = layoutInflater.inflate(R.layout.card, null)
        val nameView: TextView = view.findViewById(R.id.name)
        val db = ShoppingListDB(this.context, null)
        val id = db.addShoppingListItem(name)
        val item = Item(view, id)
        nameView.text = name
        itemList.add(item)
        layout!!.addView(view)
        Toast.makeText(
            this.context,
            name + " wurde der Einkaufsliste hinzugefügt",
            Toast.LENGTH_LONG
        ).show()
        checkIfListEmpty()
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
        checkIfListEmpty()
    }

    private fun dbDeleteItem(id: Long) {
        val db = ShoppingListDB(this.context, null)
        db.deleteShoppingListItem(id)
    }

    private fun submitItems() {
        val iterator = itemList.iterator()
        for (item in iterator) {
            if (item.view.findViewById<CheckBox>(R.id.checkbox).isChecked) {
                itemIdList.add(item.id.toInt())
            }
        }
        if (itemIdList.isEmpty()) {
            Toast.makeText(this.context, this.getString(R.string.no_article_selected), Toast.LENGTH_SHORT).show()
        } else {
            val i = Intent(activity, PurchasingActivity::class.java)
            i.putExtra("ids", itemIdList)
            startActivity(i)
        }
    }

    private fun checkIfListEmpty() {
        if (layout?.childCount!! <= 0) {
            btnDelete.isVisible = false
            btnSubmit.isVisible = false
        } else {
            btnDelete.isVisible = true
            btnSubmit.isVisible = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        checkIfListEmpty()
        super.onResume()
    }
}