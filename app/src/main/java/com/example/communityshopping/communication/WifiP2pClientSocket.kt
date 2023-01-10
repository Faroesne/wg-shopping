package com.example.communityshopping.communication

import android.content.Context
import android.util.Log
import com.example.communityshopping.communication.SocketStatus.*
import java.io.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket


class WifiP2pClientSocket(private val port: Int, private val context: Context) {

    private var socket: Socket? = null
    private var clientThread: Thread? = null
    private var status: SocketStatus = NOT_CONNECTED

    fun connectToServer(serverAddress: InetAddress) {
        Log.i("ClientSocket", "$status")
        clientThread = Thread {
            try {
                Log.i("ClientSocket", "$status in thread loop")
                when (status) {
                    NOT_CONNECTED -> {
                        Log.i("ClientSocket", "$status ot connected loop")
                        // Connect to the server
                        socket = Socket()
                        socket?.bind(null)
                        socket?.connect(InetSocketAddress(serverAddress, 8888))
                        val bufferedReader =
                            BufferedReader(InputStreamReader(socket!!.getInputStream()))
                        val message = bufferedReader.readLine()
                        Log.i("ClientSocket", "Received message: $message")
                        if (message.equals(CONNECTED.toString())) {
                            // Send SYN_ALL message data through the socket

                            //val out = BufferedWriter(OutputStreamWriter(socket?.getOutputStream()))
                            //out.write(SYNC_ALL.toString())
                            //out.newLine()
                            //out.flush()

                            val messageJSON = DbJSONWrapper(context).writeShoppingListDbJSON()

                            val outputStream = DataOutputStream(socket?.getOutputStream())
                            outputStream.writeUTF(messageJSON.toString())
                            outputStream.flush()
                            Log.i("ClientSocket", "SYNC_ALL Message sent.")

                            status = UN_SYNCHRONIZED
                        } else {
                            Log.i(
                                "ClientSocket",
                                "Unknown Message while in $status with message: $message"
                            )
                        }
                    }
                    UN_SYNCHRONIZED -> {
                        val bufferedReader =
                            BufferedReader(InputStreamReader(socket!!.getInputStream()))
                        val message = bufferedReader.readLine()
                        Log.i("ClientSocket", "Received message: $message")
                        if (message.equals(SYNC_ALL.toString())) {
                            // Send SYN_ALL message data through the socket
                            Log.i("ClientSocket", "Sync ALL here.")
                            status = SYNCHRONIZED
                        } else {
                            Log.i(
                                "ClientSocket",
                                "Unknown Message while in $status with message: $message"
                            )
                        }
                    }
                    SYNCHRONIZED -> {
                        val bufferedReader =
                            BufferedReader(InputStreamReader(socket!!.getInputStream()))
                        val message = bufferedReader.readLine()
                        if (message.equals(SYNC_ALL.toString())) {
                            // Send SYN_ALL message data through the socket
                            Log.i("ClientSocket", "Sync ALL here.")
                            status = SYNCHRONIZED
                        } else {
                            Log.i(
                                "ClientSocket",
                                "Unknown Message while in $status with message: $message"
                            )
                        }
                    }
                    else -> {
                        Log.i("ClientSocket", "Client is in an unknown status.")
                    }
                }
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