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

    fun connectToServer(serverAddress: InetAddress) {
        Log.i("ClientSocket", "${global.status}")
        clientThread = Thread {
            while (true) {
                when (global.status) {
                    NOT_CONNECTED -> {
                        // Connect to the server
                        while (global.status == NOT_CONNECTED) {
                            try {
                                socket = Socket()
                                socket?.bind(null)
                                socket?.connect(InetSocketAddress(serverAddress, 8888))
                            } catch (e: IOException) {
                                e.printStackTrace()
                                // Handle exceptions
                            }
                            Log.i("ClientSocket", "spam")
                            if (socket!!.isConnected) {
                                val input = DataInputStream(socket!!.getInputStream())
                                val jsonString = input.readUTF()
                                val jsonObject = JSONObject(jsonString)
                                val messageType = jsonObject.getString("MessageType")

                                Log.i("ClientSocket", "Received message: $messageType")
                                if (messageType.equals(CONNECTED.toString())) {
                                    // Send SYN_ALL message data through the socket
                                    val messageJSON =
                                        DbJSONWrapper(context).writeShoppingListDbJSON()

                                    val outputStream =
                                        DataOutputStream(socket?.getOutputStream())

                                    Log.i("ClientSocket", "Send message: $messageJSON")
                                    outputStream.writeUTF(messageJSON.toString())
                                    outputStream.flush()


                                    Log.i("ClientSocket", "SYNC_ALL Message sent.")

                                    global.status = UN_SYNCHRONIZED
                                } else {
                                    Log.i(
                                        "ClientSocket",
                                        "Unknown Message while in ${global.status} with message: $jsonObject"
                                    )
                                }
                            } else {
                                socket?.close()
                            }
                        }
                    }
                    UN_SYNCHRONIZED -> {
                        Log.i(
                            "ClientSocket",
                            "Reached ${global.status}"
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

                                global.status = SYNCHRONIZED
                            } else {
                                Log.i(
                                    "ClientSocket",
                                    "Unknown Message while in ${global.status} with message: $jsonObject"
                                )
                            }
                        }
                    }
                    SYNCHRONIZED -> {
                        Log.i(
                            "ClientSocket",
                            "Reached ${global.status}"
                        )
                        if (socket!!.isConnected) {
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
                                    "Unknown Message while in ${global.status} with message: $jsonObject"
                                )
                            }
                        }
                    }
                    SYNC_ALL -> {
                        val outputStream =
                            DataOutputStream(socket?.getOutputStream())
                        val messageJSON =
                            DbJSONWrapper(context).writeShoppingListDbJSON()
                        outputStream.writeUTF(messageJSON.toString())
                        outputStream.flush()

                        Log.i("ServerSocket", "SYNC_ALL Message sent: $messageJSON")

                        global.status = SYNCHRONIZED
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
        //status = NOT_CONNECTED
        clientThread?.interrupt()
        socket?.close()
    }
}