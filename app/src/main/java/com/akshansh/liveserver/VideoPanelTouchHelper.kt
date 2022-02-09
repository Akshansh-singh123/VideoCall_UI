package com.akshansh.liveserver

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd

class VideoPanelTouchHelper(
    private val topPanel:View,private val userVideoPanel:View,private val bottomPanel:View,
    private val peerVideoPanel:View,private val context:Context
): View.OnTouchListener {
    private var uX = 0f
    private var uY = 0f
    private var x = 0f
    private var y = 0f
    private var uXBound = 0f
    private var vXBound = 0f
    private var uYBound = 0f
    private var vYBound = 0f
    private var dp16 = 0f

    fun onCreate(){
        dp16 = 16.dpToPx()
    }

    fun showPanels() {
        val topAnim = ObjectAnimator.ofFloat(topPanel,"y",userVideoPanel.y)
        val bottomAnim = ObjectAnimator.ofFloat(bottomPanel,"y",
            userVideoPanel.y + userVideoPanel.height.toFloat() - bottomPanel.height.toFloat())
        val animatorSet = AnimatorSet()
        animatorSet.play(topAnim).with(bottomAnim)
        animatorSet.duration = 300
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.doOnEnd {
            snapToCorners()
        }
        animatorSet.start()
    }

    fun hidePanels() {
        val topAnim = ObjectAnimator.ofFloat(topPanel,"y",userVideoPanel.y - topPanel.height.toFloat())
        val bottomAnim = ObjectAnimator.ofFloat(bottomPanel,"y",
            userVideoPanel.y + userVideoPanel.height.toFloat())
        val animatorSet = AnimatorSet()
        animatorSet.play(topAnim).with(bottomAnim)
        animatorSet.duration = 300
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.doOnEnd {
            snapToCorners()
        }
        animatorSet.start()
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        val view = view?:return false
        if(view.id == R.id.peerVideo){
            when(motionEvent?.action){
                MotionEvent.ACTION_DOWN-> actionDown(motionEvent)
                MotionEvent.ACTION_MOVE -> actionMove(motionEvent)
                MotionEvent.ACTION_UP -> actionUp(motionEvent)
            }
        }
        return false
    }

    private fun actionDown(motionEvent: MotionEvent) {
        uX = motionEvent.rawX
        uY = motionEvent.rawY
        x = peerVideoPanel.x
        y = peerVideoPanel.y
        uXBound = userVideoPanel.x + dp16
        vXBound = userVideoPanel.x + userVideoPanel.width - peerVideoPanel.width - dp16
        uYBound = topPanel.y + topPanel.height + dp16
        vYBound = bottomPanel.y - peerVideoPanel.height - dp16
    }

    private fun actionMove(motionEvent: MotionEvent) {
        val dX = motionEvent.rawX - uX
        val dY = motionEvent.rawY - uY
        val movementX =  (x + dX).coerceIn(uXBound,vXBound)
        val movementY =  (y + dY).coerceIn(uYBound,vYBound)
        peerVideoPanel.x = movementX
        peerVideoPanel.y = movementY
    }

    private fun actionUp(motionEvent: MotionEvent) {
        x = peerVideoPanel.x
        y = peerVideoPanel.y
        snapToCorners()
    }

    private fun snapToCorners() {
        val x = peerVideoPanel.x + peerVideoPanel.width/2
        val y = peerVideoPanel.y + peerVideoPanel.height/2
        val parentCenterX = (userVideoPanel.width - userVideoPanel.x)/2
        val parentCenterY = (userVideoPanel.height - userVideoPanel.y)/2
        if(x <= parentCenterX && y <= parentCenterY){
            snapToTopLeftCorner()
        }else if(x <= parentCenterX && y > parentCenterY){
            snapToBottomLeftCorner()
        }else if(x > parentCenterX && y > parentCenterY){
            snapToBottomRightCorner()
        }else{
            snapToTopRightCorner()
        }
    }

    private fun snapToTopLeftCorner() {
        val corner = getTopLeftCorner()
        translatePeerPanelTo(corner.x,corner.y)
    }

    private fun snapToBottomLeftCorner() {
        val corner = getBottomLeftCorner()
        translatePeerPanelTo(corner.x,corner.y)
    }

    private fun snapToBottomRightCorner() {
        val corner = getBottomRightCorner()
        translatePeerPanelTo(corner.x,corner.y)
    }

    private fun snapToTopRightCorner() {
        val corner = getTopRightCorner()
        translatePeerPanelTo(corner.x,corner.y)
    }

    private fun translatePeerPanelTo(vX:Float,vY:Float){
        val animatorX = ObjectAnimator.ofFloat(peerVideoPanel,"x",vX)
        val animatorY = ObjectAnimator.ofFloat(peerVideoPanel,"y",vY)
        val animatorSet = AnimatorSet()
        animatorSet.play(animatorX).with(animatorY)
        animatorSet.duration = 100
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.start()
    }

    private fun getTopLeftCorner(): PointF {
        val cornerX = userVideoPanel.x + dp16
        val cornerY = topPanel.y + topPanel.height + dp16
        return PointF(cornerX,cornerY)
    }

    private fun getTopRightCorner(): PointF {
        val cornerX = userVideoPanel.x - dp16 + userVideoPanel.width - peerVideoPanel.width
        val cornerY = topPanel.y + topPanel.height + dp16
        return PointF(cornerX,cornerY)
    }

    private fun getBottomLeftCorner(): PointF {
        val cornerX = userVideoPanel.x + dp16
        val cornerY = bottomPanel.y - dp16 - peerVideoPanel.height
        return PointF(cornerX,cornerY)
    }

    private fun getBottomRightCorner(): PointF {
        val cornerX = userVideoPanel.x + userVideoPanel.width - dp16 - peerVideoPanel.width
        val cornerY = bottomPanel.y - dp16 - peerVideoPanel.height
        return PointF(cornerX,cornerY)
    }

    private fun Int.dpToPx() = this * context.resources.displayMetrics.density
}