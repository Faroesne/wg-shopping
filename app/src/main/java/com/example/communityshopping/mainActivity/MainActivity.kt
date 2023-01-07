package com.example.communityshopping.mainActivity

import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.communityshopping.CommunityShoppingApplication
import com.example.communityshopping.R
import com.example.communityshopping.communication.WifiP2pClientSocket
import com.example.communityshopping.communication.WifiP2pServerSocket
import com.example.communityshopping.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var global: CommunityShoppingApplication.Global

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        global = (application as CommunityShoppingApplication).global
        setupP2pSocket()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_archive,
                R.id.navigation_finances,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setupP2pSocket() {

        val connectionInfoListener = WifiP2pManager.ConnectionInfoListener { info ->
            //Check if the device is the group owner
            if (info.isGroupOwner) {
                // Device is the group owner, so start the server
                WifiP2pServerSocket(8888).startServer()
            } else {
                // Device is the client, so connect to the group owner
                WifiP2pClientSocket(8888).connectToServer(info.groupOwnerAddress)
            }
        }


        // Connect to the group owner
        global.wifiP2pManager?.requestConnectionInfo(global.wifiP2pChannel, connectionInfoListener)
    }

}
