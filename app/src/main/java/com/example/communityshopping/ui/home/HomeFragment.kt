package com.example.communityshopping.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityshopping.R
import com.example.communityshopping.TotalPriceActivity
import com.example.communityshopping.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var add: ImageButton;
    lateinit var btnDelete: ImageButton;
    lateinit var btnSubmit: Button;
    var itemList = arrayListOf<View>()
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
        buildDialog();
        add.setOnClickListener({ dialog!!.show() })
        btnDelete.setOnClickListener({ removeItems() })
        btnSubmit.setOnClickListener({ submitItems() })

        return root
    }

    private fun buildDialog(view: View = layoutInflater.inflate(R.layout.dialog, null)) {
        val builder = AlertDialog.Builder(view.getContext())
        /*val view: View = layoutInflater.inflate(R.layout.dialog, null)*/
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

    private fun addCard(name: String) {
        val view: View = layoutInflater.inflate(R.layout.card, null)
        val nameView: TextView = view.findViewById(R.id.name)
        nameView.text = name
        itemList.add(view)
        layout!!.addView(view)
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
        startActivity(Intent(getActivity(), TotalPriceActivity::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}