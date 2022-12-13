package com.example.communityshopping.mainActivity.finances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.communityshopping.R
import com.example.communityshopping.databinding.FragmentFinancesBinding

class FinancesFragment : Fragment() {

    private lateinit var financesViewModel: FinancesViewModel
    private var _binding: FragmentFinancesBinding? = null

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

        clear = root.findViewById(R.id.clearFinances)
        clear.setOnClickListener {
            Toast.makeText(this.context, this.getString(R.string.clear_finances), Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}