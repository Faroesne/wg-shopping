package com.example.communityshopping.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.communityshopping.Archive
import com.example.communityshopping.ArchiveActivity
import com.example.communityshopping.ArchiveAdapter
import com.example.communityshopping.Item
import com.example.communityshopping.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var archiveArrayList : ArrayList<Archive>
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val title = arrayOf(
            "Einkauf vom 03.10.2022",
            "Einkauf vom 05.10.2022",
            "Einkauf vom 06.10.2022",
            "Einkauf vom 09.10.2022"
        )

        val info = arrayOf(
            "3 Artikel gekauft von Nutzer1",
            "2 Artikel gekauft von Nutzer2",
            "1 Artikel gekauft von Nutzer1",
            "4 Artikel gekauft von Nutzer3"
        )

        val items = arrayListOf(
            arrayListOf("Artikel 1", "Artikel 2", "Artikel 3"),
            arrayListOf("Artikel 1", "Artikel 2"),
            arrayListOf("Artikel 1", "Artikel 2", "Artikel 3", "Artikel 4"),
            arrayListOf("Artikel 1")
        )

        val prices = arrayListOf(
            arrayListOf(1.00, 2.00, 3.00),
            arrayListOf(2.00, 1.00),
            arrayListOf(4.00, 3.00, 2.00, 1.00),
            arrayListOf(5.00)
        )

        archiveArrayList = ArrayList()

        for (i in title.indices){
            val itemsList : ArrayList<Item> = ArrayList()
            for (a in items[i].indices) {
                val itemsTemp = items[i][a]
                val pricesTemp = prices[i][a]
                itemsList.add(Item(itemsTemp, pricesTemp))
            }
            val archive = Archive(title[i], info[i], itemsList)
            archiveArrayList.add(archive)
        }

        binding.listview.isClickable = true
        binding.listview.adapter = ArchiveAdapter(requireActivity(), archiveArrayList)
        binding.listview.setOnItemClickListener { parent, view, position, id ->

            val title = title[position]
            val info = info[position]
            val items = items[position]

            val i = Intent(requireActivity(), ArchiveActivity::class.java)
            i.putExtra("archiveArrayList", archiveArrayList)
            i.putExtra("position", position)
            startActivity(i)

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}