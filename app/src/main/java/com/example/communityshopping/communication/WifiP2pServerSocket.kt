package com.example.communityshopping.communication

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

class WifiP2pServerSocket : AsyncTask<Unit, Unit, Unit>() {

    private val serverSocket: ServerSocket = ServerSocket(8980)

    fun stopServer() {
        serverSocket.close()
    }

    override fun doInBackground(vararg params: Unit?) {
        while (true) {
            val serverSocket: ServerSocket = ServerSocket(8980)
            val clientSocket = serverSocket.accept()
            val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val data = input.readLine()
            // Process the received data here.
            input.close()
            clientSocket.close()
        }
    }
}