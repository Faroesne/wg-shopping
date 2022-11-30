package com.example.communityshopping.mainActivity.archive

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.communityshopping.database.ShoppingListDB
import com.example.communityshopping.databinding.FragmentDashboardBinding
import com.example.communityshopping.mainActivity.archive.models.Archive
import java.text.SimpleDateFormat

class ArchiveFragment : Fragment() {

    private lateinit var archiveViewModel: ArchiveViewModel
    private lateinit var archiveArrayList: ArrayList<Archive>
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        archiveViewModel =
            ViewModelProvider(this).get(ArchiveViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbGetArchiveList()

        return root
    }

    private fun dbGetArchiveList() {
        val db = ShoppingListDB(this.context, null)
        val cursor = db.getArchiveData()
        archiveArrayList = ArrayList()
        if (cursor!!.count >= 1) {
            while (cursor.moveToNext()) {
                val archiveItem =
                    db.getArchiveItemData(
                        cursor.getInt
                            (cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ARCHIVE_ID))
                    )
                val archive = Archive(
                    cursor.getDouble
                        (
                        cursor.getColumnIndexOrThrow
                            (ShoppingListDB.COLUMN_ITEM_FULL_PRICE)
                    ),
                    "Einkauf vom " +
                            SimpleDateFormat
                                ("dd.MM.yyyy").format
                                (
                                cursor.getLong
                                    (
                                    cursor.getColumnIndexOrThrow
                                        (ShoppingListDB.COLUMN_ARCHIVE_DATE)
                                )
                            ),
                    archiveItem!!.count.toString() +
                            " Artikel gekauft von " + cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            ShoppingListDB.COLUMN_ARCHIVE_USERNAME
                        )
                    ),
                    cursor.getInt(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_ARCHIVE_ID))
                )
                archiveArrayList.add(archive)
            }
            cursor.close()
        }
        binding.listview.isClickable = true
        binding.listview.adapter = ArchiveAdapter(requireActivity(), archiveArrayList)
        binding.listview.setOnItemClickListener { parent, view, position, id ->

            val i = Intent(requireActivity(), ArchiveActivity::class.java)
            i.putExtra("archiveArrayList", archiveArrayList)
            i.putExtra("position", position)
            startActivity(i)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}