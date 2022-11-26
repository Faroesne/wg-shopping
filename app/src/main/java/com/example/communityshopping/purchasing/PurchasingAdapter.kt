package com.example.communityshopping.purchasing

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.communityshopping.R
import java.util.*

class PurchasingAdapter(private val context: Activity,
                        private val arrayList: ArrayList<String>)

    : ArrayAdapter<String>(context, R.layout.pricing_card, arrayList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.pricing_card, null)

        val name: TextView = view.findViewById(R.id.pricingName)

        name.text = arrayList[position]

        return view
    }
}