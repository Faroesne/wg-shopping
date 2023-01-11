package com.example.communityshopping.communication

import android.content.Context
import android.util.Log
import com.example.communityshopping.CommunityShoppingApplication
import com.example.communityshopping.communication.SocketStatus.*
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket


class WifiP2pClientSocket(
    private val port: Int,
    private val context: Context,
    private val global: CommunityShoppingApplication.Global
) {

    private var socket: Socket? = null
    private var clientThread: Thread? = null
    private var updateThread: Thread? = null
    private var status: SocketStatus = NOT_CONNECTED

    fun connectToServer(serverAddress: InetAddress) {
        Log.i("ClientSocket", "$status")
        clientThread = Thread {
            while (true) {
                when (status) {
                    NOT_CONNECTED -> {
                        // Connect to the server
                        while (status == NOT_CONNECTED) {
                            try {
                                socket = Socket()
                                socket?.bind(null)
                                socket?.connect(InetSocketAddress(serverAddress, 8888))
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            if (socket!!.isConnected) {
                                val input = DataInputStream(socket!!.getInputStream())
                                val jsonString = input.readUTF()
                                val jsonObject = JSONObject(jsonString)
                                val messageType = jsonObject.getString("MessageType")

                                Log.i("ClientSocket", "Received message: $messageType")
                                if (messageType.equals(CONNECTED.toString())) {
                                    // Send SYN_ALL message data through the socket
                                    val messageJSON =
                                        DbJSONWrapper(context).createCompleteDbJSON()

                                    val outputStream =
                                        DataOutputStream(socket?.getOutputStream())

                                    Log.i("ClientSocket", "Send message: $messageJSON")
                                    outputStream.writeUTF(messageJSON.toString())
                                    outputStream.flush()


                                    Log.i("ClientSocket", "SYNC_ALL Message sent.")

                                    status = UN_SYNCHRONIZED
                                } else {
                                    Log.i(
                                        "ClientSocket",
                                        "Unknown Message while in $status with message: $jsonObject"
                                    )
                                }
                            }
                        }
                    }
                    UN_SYNCHRONIZED -> {
                        Log.i(
                            "ClientSocket",
                            "Reached $status"
                        )
                        if (socket!!.isConnected) {
                            val input = DataInputStream(socket!!.getInputStream())
                            val jsonString = input.readUTF()
                            val jsonObject = JSONObject(jsonString)
                            val messageType = jsonObject.getString("MessageType")

                            Log.i("ClientSocket", "Received message: $jsonObject")

                            if (messageType.equals(SYNC_ALL.toString())) {
                                Log.i("ClientSocket", "Got SYNC_ALL MESSAGE")

                                //UpdateData
                                DbJSONWrapper(context).synchronizeDataWithCurrentDB(
                                    jsonObject
                                )

                                status = SYNCHRONIZED
                                updateThread = Thread {
                                    while (status == SYNCHRONIZED) {
                                        if (global.resend) {
                                            val messageJSON =
                                                DbJSONWrapper(context).createCompleteDbJSON()

                                            val outputStream =
                                                DataOutputStream(socket?.getOutputStream())

                                            Log.i("ClientSocket", "Send message: $messageJSON")
                                            outputStream.writeUTF(messageJSON.toString())
                                            outputStream.flush()


                                            Log.i("ClientSocket", "Update Executed")
                                            global.resend = false
                                        }
                                    }
                                }
                                updateThread?.start()
                                Log.i("ClientSocket", "UpdateThread started.")
                            } else {
                                Log.i(
                                    "ClientSocket",
                                    "Unknown Message while in $status with message: $jsonObject"
                                )
                            }
                        }
                    }
                    SYNCHRONIZED -> {
                        Log.i(
                            "ClientSocket",
                            "Reached $status"
                        )
                        if (socket!!.isConnected) {
                            try {


                                val input = DataInputStream(socket!!.getInputStream())
                                val jsonString = input.readUTF()
                                val jsonObject = JSONObject(jsonString)
                                val messageType = jsonObject.getString("MessageType")

                                Log.i("ClientSocket", "Received message: $jsonObject")

                                if (messageType.equals(SYNC_ALL.toString())) {
                                    // Send SYN_ALL message data through the socket
                                    Log.i("ClientSocket", "Got SYNC_ALL MESSAGE")

                                    //UpdateData
                                    DbJSONWrapper(context).synchronizeDataWithCurrentDB(jsonObject)
                                } else {
                                    Log.i(
                                        "ClientSocket",
                                        "Unknown Message while in $status with message: $jsonObject"
                                    )
                                }
                            } catch (e: Exception) {
                                Log.i(
                                    "ClientSocket",
                                    "Socket read not possible. Restarting connection"
                                )
                                reconnectToServer(serverAddress)
                            }
                        } else {
                            Log.i("ClientSocket", "Socket not connected")
                            reconnectToServer(serverAddress)
                        }
                    }
                    else -> {
                        Log.i("ClientSocket", "Client is in an unknown status.")
                    }
                }
            }
        }
        clientThread?.start()
    }

    fun disconnectFromServer() {
        clientThread?.interrupt()
        updateThread?.interrupt()
        socket?.close()
        status = NOT_CONNECTED
    }

    fun reconnectToServer(serverAddress: InetAddress) {
        status = NOT_CONNECTED
        clientThread = null
        updateThread = null
        socket?.close()
        this.connectToServer(serverAddress)
    }
}