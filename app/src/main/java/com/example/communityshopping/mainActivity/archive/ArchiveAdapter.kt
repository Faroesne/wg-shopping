package com.example.communityshopping.mainActivity.archive

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.communityshopping.R
import com.example.communityshopping.mainActivity.archive.models.Archive

class ArchiveAdapter(
    private val context: Activity,
    private val arrayList: ArrayList<Archive>
) : ArrayAdapter<Archive>(
    context,
    R.layout.archive_card, arrayList
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.archive_card, null)

        val title: TextView = view.findViewById(R.id.archive_title)
        val info: TextView = view.findViewById(R.id.archive_info)
        val paidIMG: ImageView = view.findViewById(R.id.paid_img)

        title.text = arrayList[position].title
        info.text = arrayList[position].info
        if (arrayList[position].paid == 0)
            paidIMG.visibility = INVISIBLE

        return view
    }
}