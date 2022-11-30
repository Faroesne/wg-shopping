package com.example.communityshopping.mainActivity.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.communityshopping.R
import com.example.communityshopping.databinding.FragmentNotificationsBinding
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var sprache: Button
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        loadLocate()



        settingsViewModel =
                ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sprache = root.findViewById(R.id.changeMyLang)
        sprache.setOnClickListener { showChangeLang() }



        //val textView: TextView = binding.textNotifications


        //settingsViewModel.text.observe(viewLifecycleOwner, Observer {
           // textView.text = it
        //})
        return root


    }
    private fun showChangeLang() {
        val listItems = arrayOf("English", "Deutsch")
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setTitle(this.getString(R.string.wähle_sprache_aus))
        mBuilder.setSingleChoiceItems(listItems, -1){ dialog, which ->
            if (which == 0){
                setLocate("en")
                this.activity?.recreate()
            }
            if (which == 1){
                setLocate("de")
                this.activity?.recreate()
            }
            dialog.dismiss()
        }

        val mDialog =mBuilder.create()

        mDialog.show()
    }

    private fun setLocate(Lang: String?) {

        val locale = Locale(Lang)

        Locale.setDefault(locale)

        val config = Configuration()

        config.locale = locale
        this.activity?.baseContext?.resources?.updateConfiguration(config, this.requireActivity().baseContext.resources.displayMetrics)

        val editor = this.activity?.getSharedPreferences("Settings", Context.MODE_PRIVATE)?.edit()
        if (editor != null) {
            editor.putString("My_Lang", Lang)
            editor.apply()
        }

    }

    private fun loadLocate() {
        val sharedPreferences = this.activity?.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences?.getString("My_Lang", "")
        setLocate(language)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}