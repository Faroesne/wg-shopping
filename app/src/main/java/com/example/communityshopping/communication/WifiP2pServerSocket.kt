package com.example.communityshopping.communication

import android.content.Context
import android.util.Log
import com.example.communityshopping.CommunityShoppingApplication
import com.example.communityshopping.communication.SocketStatus.*
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class WifiP2pServerSocket(
    private val port: Int,
    private val context: Context,
    private val global: CommunityShoppingApplication.Global
) {

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var serverThread: Thread? = null
    private var clientThread: Thread? = null
    private var updateThread: Thread? = null
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
            while (true) {
                try {
                    when (status) {
                        CONNECTED -> {
                            if (clientSocket!!.isConnected) {
                                val input = DataInputStream(clientSocket!!.getInputStream())
                                val jsonString = input.readUTF()
                                val jsonObject = JSONObject(jsonString)
                                val messageType = jsonObject.getString("MessageType")
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
                                    Log.i("ServerSocket", "Database Updated")

                                    updateThread = Thread {
                                        while (status == SYNCHRONIZED) {
                                            if (global.resend == 1) {
                                                val messageJSON =
                                                    DbJSONWrapper(context).writeShoppingListDbJSON()

                                                val outputStream =
                                                    DataOutputStream(clientSocket?.getOutputStream())

                                                Log.i("ServerSocket", "Send message: $messageJSON")
                                                outputStream.writeUTF(messageJSON.toString())
                                                outputStream.flush()


                                                Log.i("ServerSocket", "Update Executed")
                                                global.resend = 0
                                            }
                                        }
                                    }
                                    updateThread?.start()
                                    Log.i("ServerSocket", "UpdateThread started.")

                                    status = SYNCHRONIZED
                                } else {
                                    Log.i(
                                        "ServerSocket",
                                        "Unknown Message while in $status with message:  $jsonObject"
                                    )
                                }
                            }
                        }
                        SYNCHRONIZED -> {
                            Log.i(
                                "ServerSocket",
                                "Reached $status"
                            )
                            if (clientSocket!!.isConnected) {
                                val input = DataInputStream(clientSocket!!.getInputStream())
                                val jsonString = input.readUTF()
                                val jsonObject = JSONObject(jsonString)
                                val messageType = jsonObject.getString("MessageType")
                                Log.i("ServerSocket", "Received message: $jsonObject")
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
        }
        clientThread?.start()
    }

    fun stopServer() {
        status = NOT_CONNECTED
        serverThread?.interrupt()
        clientThread?.interrupt()
        updateThread?.interrupt()
        serverSocket?.close()
        clientSocket?.close()
    }
}