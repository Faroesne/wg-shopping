package com.example.communityshopping

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*
import kotlin.collections.ArrayList

class ArchiveDetailAdapter(private val context : Activity,
                           private val arrayList: ArrayList<Item>) : ArrayAdapter<Item>(context, R.layout.archivearticles_card, arrayList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.archivearticles_card, null)

        val product : TextView = view.findViewById(R.id.archive_product)
        val price : TextView = view.findViewById(R.id.archive_price)

        /*val items = arrayOf(
            arrayOf("Artikel 1", "Artikel 2", "Artikel 3"),
            arrayOf("Artikel 1", "Artikel 2"),
            arrayOf("Artikel 1", "Artikel 2", "Artikel 3", "Artikel 4"),
            arrayOf("Artikel 1")
        )

        val prices = arrayOf(
            arrayOf("1,00€", "2,00€", "3,00€"),
            arrayOf("2,00€", "1,00€"),
            arrayOf("4,00€", "3,00€", "2,00€", "1,00€"),
            arrayOf("5,00€")
        )*/

        product.text = arrayList[position].articleName
        price.text = "%,.2f".format(Locale.GERMAN, arrayList[position].price) + "€"

        /*product.text = items[position][0]
        price.text = prices[position][0]*/

        return view
    }
}