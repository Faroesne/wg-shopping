package com.example.communityshopping.communication

import android.content.Context
import android.util.Log
import com.example.communityshopping.communication.SocketStatus.*
import org.json.JSONObject
import java.io.*
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class WifiP2pServerSocket(private val port: Int, private val context: Context) {

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var serverThread: Thread? = null
    private var clientThread: Thread? = null
    private var status: SocketStatus = NOT_CONNECTED

    fun startServer() {
        Log.i("ServerSocket", "$status")
        serverThread = Thread {
                try {
                    // Bind the server socket to a specific port
                    if (status == NOT_CONNECTED) {
                        serverSocket = ServerSocket()
                        serverSocket?.bind(InetSocketAddress(8888))

                        // Accept incoming client connections
                        clientSocket = serverSocket?.accept()
                        Log.i(
                            "ServerSocket",
                            "Connection to clientsocket " + clientSocket.toString()
                        )
                        // Start the client thread to handle the connection

                        val outputStream = DataOutputStream(clientSocket?.getOutputStream())

                        val dbShoppingListJSON = JSONObject()
                        dbShoppingListJSON.put("MessageType", CONNECTED)

                        outputStream.writeUTF(dbShoppingListJSON.toString())
                        outputStream.flush()

                        Log.i("ServerSocket", "$CONNECTED message send.")
                        status = CONNECTED
                        startClientThread(clientSocket)
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
                    val input = DataInputStream(clientSocket!!.getInputStream())
                    val jsonString = input.readUTF()
                    val jsonObject = JSONObject(jsonString)
                    val messageType = jsonObject.getString("MessageType")

                    Log.i("JSON", jsonObject.toString())
                    Log.i("ServerSocket", messageType)

                    when (status) {
                        CONNECTED -> {
                                Log.i("ServerSocket", "Received message: $jsonObject")
                                if (messageType.equals(SYNC_ALL.toString())) {
                                    Log.i("ServerSocket", "Got SYNC_ALL MESSAGE")
                                    // Send SYN_ALL message data through the socket
                                    val outputStream =
                                        DataOutputStream(clientSocket?.getOutputStream())
                                    val messageJSON =
                                        DbJSONWrapper(context).writeShoppingListDbJSON()
                                    outputStream.writeUTF(messageJSON.toString())
                                    outputStream.flush()

                                    Log.i("ServerSocket", "SYNC_ALL Message sent: $messageJSON")

                                    //UpdateData
                                    DbJSONWrapper(context).synchronizeDataWithCurrentDB(jsonObject)

                                    status = SYNCHRONIZED
                                } else {
                                    Log.i(
                                        "ServerSocket",
                                        "Unknown Message while in $status with message:  $jsonObject"
                                    )
                                }

                        }
                        SYNCHRONIZED -> {
                            val input = DataInputStream(clientSocket!!.getInputStream())
                            val jsonString = input.readUTF()
                            val jsonObject = JSONObject(jsonString)
                            val messageType = jsonObject.getString("MessageType")

                            if (messageType.equals(SYNC_ALL.toString())) {
                                Log.i("ServerSocket", "Got SYNC_ALL MESSAGE")

                                //UpdateData
                                DbJSONWrapper(context).synchronizeDataWithCurrentDB(jsonObject)
                            } else {
                                Log.i(
                                    "ServerSocket",
                                    "Unknown Message while in $status with message: $jsonObject"
                                )
                            }
                        }
                        else -> {
                            Log.i("ServerSocket", "Client is in an unknown status.")
                        }
                    }
                } catch (e: IOException) {
                    // Handle exceptions
                     e.printStackTrace()
                }

        }
        clientThread?.start()
    }

    fun stopServer() {
        status = NOT_CONNECTED
        serverThread?.interrupt()
        clientThread?.interrupt()
        serverSocket?.close()
        clientSocket?.close()
    }
}