package com.akshansh.liveserver

import android.app.IntentService
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log

class SocketServiceBackground:IntentService("work"){
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onHandleIntent(p0: Intent?) {
        (1..10).forEach{
            Log.e(TAG, "onStartCommand: Disconnecting from server in ${10-it} s")
            SystemClock.sleep(1000)
        }
        stopSelf()
    }

    companion object{
        private const val TAG = "SocketServiceBackground"
    }
}