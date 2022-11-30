package com.example.communityshopping.mainActivity.archive

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.communityshopping.R
import java.util.*

class ArchiveDetailAdapter(
    private val context: Activity,
    private val arrayList: ArrayList<archiveItem>
) : ArrayAdapter<archiveItem>(
    context,
    R.layout.archivearticles_card, arrayList
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.archivearticles_card, null)

        val product: TextView = view.findViewById(R.id.archive_product)
        val price: TextView = view.findViewById(R.id.archive_price)

        product.text = arrayList[position].articleName
        if (arrayList[position].price != null) {
            price.text = "%,.2f".format(Locale.GERMAN, arrayList[position].price) + "€"
        }

        return view
    }
}