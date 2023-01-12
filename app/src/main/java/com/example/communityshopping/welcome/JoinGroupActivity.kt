package com.example.communityshopping.welcome

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.communityshopping.CommunityShoppingApplication
import com.example.communityshopping.R
import com.example.communityshopping.mainActivity.MainActivity

class JoinGroupActivity : AppCompatActivity() {

    lateinit var joinBtn: Button
    var linearLayout: LinearLayout? = null

    lateinit var view: View

    private lateinit var wifiManager: WifiManager
    private lateinit var wifiDirectBroadcastReceiver: BroadcastReceiver
    private lateinit var global: CommunityShoppingApplication.Global
    private var connectionEstablished = false

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_group)

        global = (application as CommunityShoppingApplication).global

        joinBtn = findViewById(R.id.gruppeBeitreten2)
        joinBtn.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        linearLayout = findViewById(R.id.containerList)

        setupWifiP2P()
        searchPeers()

        registerReceiver(wifiDirectBroadcastReceiver, intentFilter)

        window.navigationBarColor = Color.WHITE
    }

    private fun setupWifiP2P() {
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        global.wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        global.wifiP2pChannel = global.wifiP2pManager!!.initialize(this, mainLooper, null)

        val peerListListener = WifiP2pManager.PeerListListener { peerList ->
            // Update the linear layout on the UI thread
            runOnUiThread {
                // Clear the linear layout
                linearLayout?.removeAllViews()

                // Add a view for each device_card found in the peer list
                for (device in peerList.deviceList) {
                    val view: View = layoutInflater.inflate(R.layout.device_card, null)
                    val nameView: TextView = view.findViewById(R.id.name)
                    nameView.text = device.deviceName

                    view.setOnClickListener {
                        val config = WifiP2pConfig().apply {
                            deviceAddress = device.deviceAddress
                        }
                        global.wifiP2pManager!!.connect(
                            global.wifiP2pChannel,
                            config,
                            object : WifiP2pManager.ActionListener {
                                override fun onSuccess() {
                                    Toast.makeText(
                                        view.context,
                                        "Connection initiated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onFailure(p0: Int) {
                                    Toast.makeText(
                                        view.context,
                                        "Connection failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                    checkDeviceStatus(device, view)

                    linearLayout?.addView(view)
                }
            }

        }

        wifiDirectBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
                    // Check if WiFi Direct is enabled
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        Toast.makeText(
                            this@JoinGroupActivity,
                            "WiFi Direct is enabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@JoinGroupActivity,
                            "WiFi Direct is disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {
                    // Request the updated list of peers
                    global.wifiP2pManager!!.requestPeers(global.wifiP2pChannel, peerListListener)
                }
            }
        }
        global.wifiP2pManager!!.requestPeers(global.wifiP2pChannel, peerListListener)
    }

    private fun checkDeviceStatus(device: WifiP2pDevice?, view: View) {
        if (device != null && !connectionEstablished) {
            if (device.status == WifiP2pDevice.CONNECTED) {
                val nameView: TextView = view.findViewById(R.id.connectionStatus)
                nameView.text = resources.getString(R.string.device_status_connected)
                connectionEstablished = true
                Thread.sleep(2000)
                startActivity(Intent(this, MainActivity::class.java))
            } else if (device.status == WifiP2pDevice.AVAILABLE) {
                val nameView: TextView = view.findViewById(R.id.connectionStatus)
                nameView.text = resources.getString(R.string.device_status_available)
            } else if (device.status == WifiP2pDevice.INVITED) {
                val nameView: TextView = view.findViewById(R.id.connectionStatus)
                nameView.text = resources.getString(R.string.device_status_invited)
            }

        }
    }

    private fun searchPeers() {
        // Start the search for devices
        global.wifiP2pManager?.discoverPeers(
            global.wifiP2pChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Toast.makeText(this@JoinGroupActivity, "Discovery started", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFailure(reason: Int) {
                    Toast.makeText(
                        this@JoinGroupActivity,
                        "Discovery failed: $reason",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        //startActivity(Intent(this, MainActivity::class.java))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

    }
}