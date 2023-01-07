package com.example.communityshopping.communication

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class WifiP2pServerSocket(private val port: Int) {

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var serverThread: Thread? = null
    private var clientThread: Thread? = null

    fun startServer() {
        serverThread = Thread {
            try {
                // Bind the server socket to a specific port
                serverSocket = ServerSocket()
                serverSocket?.bind(InetSocketAddress(8888))

                // Accept incoming client connections
                clientSocket = serverSocket?.accept()
                Log.i("WIFIP2P", "Connection to clientsocket" + clientSocket.toString())
                // Start the client thread to handle the connection
                startClientThread(clientSocket)
            } catch (e: IOException) {
                // Handle exceptions
            }
        }
        serverThread?.start()
    }

    private fun startClientThread(clientSocket: Socket?) {
        clientThread = Thread {
            try {
                // Send and receive data through the client socket
                val `in` = BufferedReader(InputStreamReader(clientSocket?.getInputStream()))
                val message = `in`.readLine()
                Log.i("ServerSocket", message)
            } catch (e: IOException) {
                // Handle exceptions
            }
        }
        clientThread?.start()
    }

    fun stopServer() {
        serverThread?.interrupt()
        clientThread?.interrupt()
        serverSocket?.close()
        clientSocket?.close()
    }
}