package com.akshansh.liveserver

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var userVideoPanel:ConstraintLayout
    private lateinit var peerVideoPanel:CardView
    private lateinit var topPanel:ConstraintLayout
    private lateinit var bottomPanel:ConstraintLayout
    private lateinit var motionLayout:ConstraintLayout
    private lateinit var videoPanelTouchHelper: VideoPanelTouchHelper
    private var isInPictureMode = false
    private var hidden = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        userVideoPanel = findViewById(R.id.videoPanel)
        peerVideoPanel = findViewById(R.id.peerVideo)
        motionLayout = findViewById(R.id.motionLayout)
        topPanel = findViewById(R.id.topPanel)
        bottomPanel = findViewById(R.id.bottomPanel)

        videoPanelTouchHelper = VideoPanelTouchHelper(topPanel,userVideoPanel,bottomPanel,peerVideoPanel,this)
        videoPanelTouchHelper.onCreate()

        userVideoPanel.setOnClickListener(this)
        peerVideoPanel.setOnClickListener(this)
        peerVideoPanel.setOnTouchListener(videoPanelTouchHelper)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.videoPanel->{
                if(!hidden) videoPanelTouchHelper.hidePanels()
                else videoPanelTouchHelper.showPanels()
                hidden = !hidden
            }
        }
    }

    override fun onUserLeaveHint() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params: PictureInPictureParams = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(1, 2)).build()
            enterPictureInPictureMode(params)
        }
        super.onUserLeaveHint()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        isInPictureMode = isInPictureInPictureMode
        if(isInPictureMode){
            topPanel.visibility = View.INVISIBLE
            bottomPanel.visibility = View.INVISIBLE
            peerVideoPanel.visibility = View.INVISIBLE
        }else{
            topPanel.visibility = View.VISIBLE
            bottomPanel.visibility = View.VISIBLE
            peerVideoPanel.visibility = View.VISIBLE
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    override fun onBackPressed() {
        if(isInPictureMode){
            super.onBackPressed()
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val params: PictureInPictureParams = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(1, 2)).build()
                enterPictureInPictureMode(params)
            }else{
                super.onBackPressed()
            }
        }
    }
}