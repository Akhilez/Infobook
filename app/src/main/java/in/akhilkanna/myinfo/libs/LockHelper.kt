package `in`.akhilkanna.myinfo.libs

import `in`.akhilkanna.myinfo.R
import `in`.akhilkanna.myinfo.security.Pin
import android.content.Context
import android.graphics.Rect
import android.support.design.widget.BottomSheetBehavior
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.pin_sheet.view.*

class LockHelper(context: Context, private val lockLayout: View) {

    private var pin: Pin = Pin(context)
    private val listener: PinListener
    private val sheetBehavior: BottomSheetBehavior<View>

    init {
        if (context is PinListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement PinListener")
        }

        sheetBehavior = BottomSheetBehavior.from(lockLayout)

        val rootView = lockLayout
        val pins = arrayOf(rootView.pin0, rootView.pin1, rootView.pin2, rootView.pin3, rootView.pin4, rootView.pin5, rootView.pin6, rootView.pin7, rootView.pin8, rootView.pin9)

        for ((index, pinView) in pins.withIndex()) {

            pinView?.setOnClickListener {
                highlightDot(true)

                if (pin.enter(index)) {
                    listener.pinSuccess()
                    destroy()
                } else if (pin.numEntered() == 4) {
                    listener.pinFailed()
                    destroy()
                }
            }
        }
        rootView.pinBack?.setOnClickListener {
            pin.back()
            highlightDot(false)
        }

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        listener.onHidden()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        listener.onExpanded()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        listener.onCollapsed(); destroy()
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        listener.onDragging()
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        listener.onSettling()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }


    private fun highlightDot(highlight: Boolean) {
        val highlightColor = R.drawable.ic_lens_black_24dp
        val dimColor = R.drawable.ic_panorama_fish_eye_black_24dp
        if (lockLayout.dot4.tag == "1") {
            if (!highlight) {
                lockLayout.dot4.tag = "0"
                lockLayout.dot4.setImageResource(dimColor)
            }
        } else if (lockLayout.dot3.tag == "1") {
            if (highlight) {
                lockLayout.dot4.tag = "1"
                lockLayout.dot4.setImageResource(highlightColor)
            } else {
                lockLayout.dot3.tag = "0"
                lockLayout.dot3.setImageResource(dimColor)
            }
        } else if (lockLayout.dot2.tag == "1") {
            if (highlight) {
                lockLayout.dot3.tag = "1"
                lockLayout.dot3.setImageResource(highlightColor)
            } else {
                lockLayout.dot2.tag = "0"
                lockLayout.dot2.setImageResource(dimColor)
            }
        } else if (lockLayout.dot1.tag == "1") {
            if (highlight) {
                lockLayout.dot2.tag = "1"
                lockLayout.dot2.setImageResource(highlightColor)
            } else {
                lockLayout.dot1.tag = "0"
                lockLayout.dot1.setImageResource(dimColor)
            }
        } else if (highlight) {
            lockLayout.dot1.tag = "1"
            lockLayout.dot1.setImageResource(highlightColor)
        }
    }

    fun lock() {
        pin.lock()
    }

    fun destroy() {
        pin.destroy()
        highlightDot(false)
        highlightDot(false)
        highlightDot(false)
        highlightDot(false)
    }

    fun isLocked() = pin.isLocked()
    fun isExpanded() = sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
    fun closeSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun openSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun handleOutsideTouch(event: MotionEvent?): Boolean {
        if (event == null) return true
        if (!(event.action == MotionEvent.ACTION_DOWN && isExpanded())) return true
        val outRect = Rect()
        lockLayout.getGlobalVisibleRect(outRect)
        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            closeSheet()
            return false
        }
        return true
    }

    interface PinListener {
        fun pinSuccess()
        fun pinFailed()
        fun onHidden() {}
        fun onExpanded() {}
        fun onCollapsed() {}
        fun onDragging() {}
        fun onSettling() {}
    }

}