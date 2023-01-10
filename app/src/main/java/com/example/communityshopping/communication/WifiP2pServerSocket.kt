package com.example.communityshopping.communication

import android.util.Log
import com.example.communityshopping.communication.SocketStatus.*
import java.io.*
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class WifiP2pServerSocket(private val port: Int) {

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var serverThread: Thread? = null
    private var clientThread: Thread? = null
    private var status: SocketStatus = NOT_CONNECTED

    fun startServer() {
        serverThread = Thread {
            try {
                // Bind the server socket to a specific port
                if (status.equals(NOT_CONNECTED.toString())) {
                    serverSocket = ServerSocket()
                    serverSocket?.bind(InetSocketAddress(8888))

                    // Accept incoming client connections
                    clientSocket = serverSocket?.accept()
                    Log.i(
                        "ServerSocket",
                        "Connection to clientsocket " + clientSocket.toString()
                    )
                    // Start the client thread to handle the connection
                    startClientThread(clientSocket)
                    val out =
                        BufferedWriter(OutputStreamWriter(clientSocket?.getOutputStream()))
                    out.write(CONNECTED.toString())
                    out.newLine()
                    out.flush()
                    Log.i("ServerSocket", "$CONNECTED message send.")
                    status = CONNECTED
                }
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
                when (status) {
                    CONNECTED -> {
                        if (message.equals(SYNC_ALL.toString())) {
                            // Send SYN_ALL message data through the socket
                            Log.i("ServerSocket", "Sync ALL here.")
                            status = SYNCHRONIZED
                        } else {
                            Log.i(
                                "ServerSocket",
                                "Unknown Message while in $status with message: $message"
                            )
                        }
                    }
                    SYNCHRONIZED -> {
                        if (message.equals(SYNC_ALL.toString())) {
                            // Send SYN_ALL message data through the socket
                            Log.i("ServerSocket", "Sync ALL here.")
                            status = SYNCHRONIZED
                        } else {
                            Log.i(
                                "ServerSocket",
                                "Unknown Message while in $status with message: $message"
                            )
                        }
                    }
                    else -> {
                        Log.i("ServerSocket", "Client is in an unknown status.")
                    }
                }
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