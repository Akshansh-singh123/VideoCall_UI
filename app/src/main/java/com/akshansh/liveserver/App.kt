package com.akshansh.liveserver

import android.app.Application
import timber.log.Timber

class App:Application() {
    private lateinit var socketManager:SocketManager
    fun getSocketManager() = socketManager

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        socketManager = SocketManager(this)
//        socketManager.connect()
    }

    override fun onTerminate() {
//        socketManager.disconnect()
        super.onTerminate()
    }
}