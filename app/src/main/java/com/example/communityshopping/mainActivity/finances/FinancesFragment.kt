package com.example.communityshopping.mainActivity.finances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.database.getDoubleOrNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.communityshopping.R
import com.example.communityshopping.database.ShoppingListDB
import com.example.communityshopping.databinding.FragmentFinancesBinding
import java.util.*

class FinancesFragment : Fragment() {

    private lateinit var financesViewModel: FinancesViewModel
    private var _binding: FragmentFinancesBinding? = null
    var scroll: LinearLayout? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var clear: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        financesViewModel =
            ViewModelProvider(this).get(FinancesViewModel::class.java)

        _binding = FragmentFinancesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        scroll = root.findViewById(R.id.financeList)
        clear = root.findViewById(R.id.clearFinances)
        clear.setOnClickListener {
            clearFinance()
        }

        addUserList()
        return root
    }

    private fun addUserList() {
        val db = ShoppingListDB(this.context, null)
        val cursor = db.getUserFinancesData()
        if (cursor!!.count >= 1) {
            while (cursor.moveToNext()) {
                val view: View = layoutInflater.inflate(R.layout.finances_card, null)
                val textViewName: TextView = view.findViewById(R.id.finances_name)
                val textViewFinances: TextView = view.findViewById(R.id.finances_money)
                if (cursor != null) {
                    textViewName.text =
                        cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_USER_NAME))
                    textViewFinances.text =
                        "%,.2f".format(
                            Locale.GERMAN,
                            cursor.getDoubleOrNull(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_USER_FINANCES))
                        ) + "€"
                }
                scroll!!.addView(view)
            }
            cursor.close()
        }
    }

    private fun clearFinance() {
        val db = ShoppingListDB(this.context, null)
        db.clearFinance()
        scroll!!.removeAllViews()
        addUserList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}