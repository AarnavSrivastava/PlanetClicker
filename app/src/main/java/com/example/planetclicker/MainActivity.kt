package com.example.planetclicker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MotionEventCompat
import app.rive.runtime.kotlin.RiveAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.floor

class MainActivity : AppCompatActivity() {
    var metal: AtomicInteger = AtomicInteger(0)
    var currentPerClick: AtomicInteger = AtomicInteger(1)
    var multiplier: AtomicReference<Double> = AtomicReference<Double>(1.0)

    lateinit var mainActivity: ConstraintLayout
    lateinit var disp: MaterialTextView
    lateinit var button: MaterialButton
    lateinit var rav: RiveAnimationView
    lateinit var touchRav: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        disp = findViewById(R.id.label)
        button = findViewById(R.id.button)
        mainActivity = findViewById(R.id.layout)
        touchRav = findViewById(R.id.touchRav)
        rav = findViewById<RiveAnimationView>(R.id.rav)

        touchRav.apply {
            setOnTouchListener(@SuppressLint("ClickableViewAccessibility")
            object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    val action: Int = MotionEventCompat.getActionMasked(event)

                    return when (action) {
                        MotionEvent.ACTION_DOWN -> {
                            rav.setBooleanState("State Machine 1", "Pressed", true)

                            disp.text = "Metal: ${metal.addAndGet(floor(currentPerClick.toInt() * multiplier.toString().toDouble() + 0.5).toInt())}"

                            true
                        }
                        MotionEvent.ACTION_UP -> {
                            rav.setBooleanState("State Machine 1", "Pressed", false)
//                            touchRav.isEnabled = false
//                            sleepThread()

                            true
                        }
                        else -> true
                    }
                }
            })
        }


        button.setOnClickListener {
            var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(this)
            var bottomSheetView: View = LayoutInflater.from(applicationContext).inflate(R.layout.upgrade_sheet, mainActivity, false)

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }

//    fun sleepThread() {
//        Handler(Looper.getMainLooper()).postDelayed(
//            {
//                touchRav.isEnabled = true
//            },
//            50
//        )
//    }
}