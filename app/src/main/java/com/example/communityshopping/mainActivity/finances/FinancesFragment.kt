package com.example.communityshopping.mainActivity.finances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.communityshopping.CommunityShoppingApplication
import com.example.communityshopping.R
import com.example.communityshopping.database.ShoppingListDB
import com.example.communityshopping.databinding.FragmentFinancesBinding
import java.math.RoundingMode
import java.util.*

class FinancesFragment : Fragment() {

    private lateinit var financesViewModel: FinancesViewModel
    private var _binding: FragmentFinancesBinding? = null
    var scroll: LinearLayout? = null
    private lateinit var global: CommunityShoppingApplication.Global

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
        global = (activity?.application as CommunityShoppingApplication).global
        scroll = root.findViewById(R.id.financeList)
        clear = root.findViewById(R.id.clearFinances)
        clear.setOnClickListener {
            clearFinance()
        }

        addUserList()
        return root
    }

    /**
     * This method retrieves the user list from the database
     */

    private fun addUserList() {
        val db = ShoppingListDB(this.context, null)
        val cursor = db.getUserFinancesData()
        if (cursor!!.count >= 1) {
            while (cursor.moveToNext()) {
                val view: View = layoutInflater.inflate(R.layout.finances_card, null)
                val textViewName: TextView = view.findViewById(R.id.finances_name)
                val textViewFinances: TextView = view.findViewById(R.id.finances_money)
                val username =
                    cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListDB.COLUMN_USER_NAME))
                textViewName.text = username
                val openFinance = db.getArchivesToBePaid()
                val paidFinance = db.getArchivesUserPaid(username)
                var personalFinance = paidFinance - (openFinance / cursor.count)
                personalFinance =
                    personalFinance.toBigDecimal().setScale(2, RoundingMode.DOWN).toDouble()
                textViewFinances.text =
                    "%,.2f".format(
                        Locale.GERMAN,
                        personalFinance
                    ) + "€"
                scroll!!.addView(view)
            }
            cursor.close()
        }
    }

    /**
     * This method clears the finances for the users
     */

    private fun clearFinance() {
        val db = ShoppingListDB(this.context, null)
        db.clearFinance()
        global.resend = true
        scroll!!.removeAllViews()
        addUserList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}