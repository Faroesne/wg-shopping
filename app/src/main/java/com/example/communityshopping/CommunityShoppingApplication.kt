package com.example.communityshopping

import android.app.Application
import android.net.wifi.p2p.WifiP2pManager
import com.example.communityshopping.communication.SocketStatus

class CommunityShoppingApplication : Application() {

    // Global variable to hold the WifiP2pManager object
    val global = Global()

    class Global {
        var wifiP2pManager: WifiP2pManager? = null
        var wifiP2pChannel: WifiP2pManager.Channel? = null
        var status: SocketStatus = SocketStatus.NOT_CONNECTED
    }
}