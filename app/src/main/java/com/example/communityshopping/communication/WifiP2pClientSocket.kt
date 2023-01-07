package com.example.communityshopping.communication

import android.util.Log
import java.io.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket


class WifiP2pClientSocket(private val port: Int) {

    private var socket: Socket? = null
    private var clientThread: Thread? = null

    fun connectToServer(serverAddress: InetAddress) {
        clientThread = Thread {
            try {
                // Connect to the server
                socket = Socket()
                socket?.bind(null)
                socket?.connect(InetSocketAddress(serverAddress, 8888))

                // Send and receive data through the socket
                val out = BufferedWriter(OutputStreamWriter(socket?.getOutputStream()))
                out.write("Hello World")
                out.newLine()
                out.flush()
                Log.i("ClientSocket", "Message sent.")

                val `in` = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                val message = `in`.readLine()
                Log.i("ClientSocket", "Message received: " + message)

            } catch (e: IOException) {
                // Handle exceptions
            }
        }
        clientThread?.start()
    }

    fun disconnectFromServer() {
        clientThread?.interrupt()
        socket?.close()
    }
}