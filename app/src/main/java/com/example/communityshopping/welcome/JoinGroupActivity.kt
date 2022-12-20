package com.example.communityshopping.welcome

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.communityshopping.R
import com.example.communityshopping.mainActivity.MainActivity

class JoinGroupActivity : AppCompatActivity() {

    lateinit var joinBtn: Button
    var totalLayout: LinearLayout? = null
    lateinit var view: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_group)
        joinBtn = findViewById(R.id.gruppeBeitreten2)
        joinBtn.setOnClickListener { joinGroup() }
        totalLayout = findViewById(R.id.containerList)

        //registerReceiver(mReceiver, intentFilter)

        window.setNavigationBarColor(Color.WHITE)
    }

    private fun joinGroup() {
        saveBluetoothStatus()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*
    var mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                var device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                Log.i("BluetoothReceiver", "found")
                if (ActivityCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }

                if (!deviceList.contains(device)) {
                    deviceList.add(device!!)
                    var view: View = layoutInflater.inflate(R.layout.group_card, null)
                    var textView: TextView = view.findViewById(R.id.groupName)
                    textView.text = device!!.name
                    totalLayout!!.addView(view)
                }

                //deviceList.add(device!!.name)
            } else if (action.equals(
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                )
            ) {
            } else if (action.equals(
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED
                )
            ) { }
        }
    } */

    private fun saveBluetoothStatus() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.app_preferences), Context.MODE_PRIVATE
        )
        with(sharedPref.edit()) {
            putInt(getString(R.string.bluetooth_setup_status), 1)
            Log.i("sharedPref", "UserName gespeichert.")
            apply()
        }
    }
}