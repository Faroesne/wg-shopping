package com.example.communityshopping.communication

import android.os.AsyncTask
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket


class WifiP2pClientSocket(private val groupOwnerAddress: InetAddress) :
    AsyncTask<Unit, Unit, Unit>() {

    private val socket: Socket = Socket(groupOwnerAddress, 8980)

    fun stopClient() {
        socket.close()
    }

    override fun doInBackground(vararg params: Unit?) {
        val socket: Socket = Socket(groupOwnerAddress, 8980)
        val out = PrintWriter(socket.getOutputStream(), true)
        out.println("Hello, world!")
        out.close()
        socket.close()
    }
}