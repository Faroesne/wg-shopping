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

/**

The WifiP2pServerSocket class is used to establish a socket connection with a client,

and to synchronize data between the server and the client.

@param port the port number to use for the socket connection

@param context the context of the application

@param global the global application object

@property serverSocket the server socket used for the connection

@property clientSocket the client socket used for the connection

@property serverThread the thread used to handle the server side of the connection

@property clientThread the thread used to handle the client side of the connection

@property updateThread the thread used to handle updates to the connection

@property status the current status of the socket connection, represented by the SocketStatus enum (NOT_CONNECTED, CONNECTED, UN_SYNCHRONIZED, SYNCHRONIZED, SYNC_ALL)
 */
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

    /**

    Start the server, bind it to a specific port and wait for incoming connections from clients.
     */
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

    /**

    Start the server, bind it to a specific port and wait for incoming connections from clients.

    @param clientSocket the client socket used for the connection
     */
    private fun startClientThread(clientSocket: Socket?) {
        clientThread = Thread {
            while (status != NOT_CONNECTED) {
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
                                        DbJSONWrapper(context).createCompleteDbJSON()
                                    outputStream.writeUTF(messageJSON.toString())
                                    outputStream.flush()

                                    Log.i("ServerSocket", "SYNC_ALL Message sent: $messageJSON")

                                    //UpdateData
                                    DbJSONWrapper(context).synchronizeDataWithCurrentDB(jsonObject)
                                    Log.i("ServerSocket", "Database Updated")

                                    updateThread = Thread {
                                        while (status == SYNCHRONIZED) {
                                            if (global.resend) {
                                                val messageJSON =
                                                    DbJSONWrapper(context).createCompleteDbJSON()

                                                val outputStream =
                                                    DataOutputStream(clientSocket?.getOutputStream())

                                                Log.i("ServerSocket", "Send message: $messageJSON")
                                                outputStream.writeUTF(messageJSON.toString())
                                                outputStream.flush()


                                                Log.i("ServerSocket", "Update Executed")
                                                global.resend = false
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
                                try {
                                    val input = DataInputStream(clientSocket!!.getInputStream())

                                    val jsonString = input.readUTF()
                                    val jsonObject = JSONObject(jsonString)
                                    val messageType = jsonObject.getString("MessageType")
                                    Log.i("ServerSocket", "Received message: $jsonObject")
                                    if (messageType.equals(SYNC_ALL.toString())) {
                                        Log.i("ServerSocket", "Got SYNC_ALL MESSAGE")

                                        //UpdateData
                                        DbJSONWrapper(context).synchronizeDataWithCurrentDB(
                                            jsonObject
                                        )
                                    } else {
                                        Log.i(
                                            "ServerSocket",
                                            "Unknown Message while in $status with message: $jsonObject"
                                        )
                                    }

                                } catch (e: Exception) {
                                    Log.i(
                                        "ServerSocket",
                                        "Socket read not possible. Restarting connection"
                                    )
                                    restartServer()
                                }
                            } else {
                                Log.i("ServerSocket", "Socket not connected")
                                restartServer()
                            }
                        }
                        else -> {
                            Log.i("ServerSocket", "Client is in an unknown status. $status")
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
        serverSocket?.close()
        clientSocket?.close()
    }

    fun restartServer() {
        status = NOT_CONNECTED
        updateThread = null
        clientThread = null
        serverThread = null
        serverSocket?.close()
        clientSocket?.close()
        //serverSocket = null
        //clientSocket = null
        this.startServer()
    }
}