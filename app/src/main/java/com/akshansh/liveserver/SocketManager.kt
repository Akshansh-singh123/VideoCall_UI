package com.akshansh.liveserver

import android.content.Context
import io.socket.client.IO
import io.socket.client.Socket
import timber.log.Timber
import kotlin.random.Random

class SocketManager(private val context: Context) {
    interface Listener{
        fun onConnected()
        fun onBroadcastMessageReceived(message:String)
        fun onConnectionFailure()
        fun onDisconnected()
    }

    private var socket:Socket? = null
    private val listeners = mutableSetOf<Listener>()

    fun connect() {
        try {
            socket = IO.socket(URL)
            setEvents()
            val socket = socket?:return
            socket.connect()
        }catch (e:Exception){
            listeners.forEach { it.onConnectionFailure() }
        }
    }

    private fun setEvents() {
        val socket = socket?:return
        socket.on(Socket.EVENT_CONNECT){
            Timber.e("Connected")
            socket.emit(REGISTER,"${Random.nextLong(9000000000L,9999999999L)}")
            listeners.forEach { it.onConnected() }
        }.on(BROADCAST){ args->
            if(args.isNotEmpty()) {
                Timber.e(args[0]?.toString())
                listeners.forEach { it.onBroadcastMessageReceived(args[0].toString()) }
            }
        }.on(Socket.EVENT_DISCONNECT){
            Timber.e("Disconnected")
            listeners.forEach { it.onDisconnected() }
        }
    }

    fun sendMessageToRoom(room:String,message: String){
        socket?.to(room)?.first?.emit("message",message)
    }

    fun disconnect() {
        Timber.e("disconnect: Disconnecting..")
        socket?.disconnect()
    }

    fun registerListener(listener:Listener){
        listeners.add(listener)
    }

    fun unregisterListener(listener:Listener){
        listeners.remove(listener)
    }

    companion object{
        private const val URL = "http://10.0.2.2:3000"
        private const val BROADCAST = "broadcast"
        private const val REGISTER = "register"
    }
}