package com.example.planetclicker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.rive.runtime.kotlin.RiveAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
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

    var items: ArrayList<UpgradeItem> = arrayListOf(UpgradeItem("Manual Labor", R.drawable.miner, 30))
    var upgradeItemAdapter = UpgradeItemAdapter(items)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        disp = findViewById(R.id.label)
        button = findViewById(R.id.button)
        mainActivity = findViewById(R.id.layout)
        touchRav = findViewById(R.id.touchRav)
        rav = findViewById(R.id.rav)

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
                            true
                        }
                        else -> true
                    }
                }
            })
        }


        button.setOnClickListener {
            var bottomSheetDialog = BottomSheetDialog(this)

            var bottomSheetView: View = LayoutInflater.from(applicationContext).inflate(R.layout.upgrade_sheet, mainActivity, false)

            bottomSheetDialog.setContentView(bottomSheetView)

            var upgradeView: RecyclerView? = bottomSheetDialog.findViewById(R.id.recyclerUpgrades)
            upgradeView?.adapter = upgradeItemAdapter
            upgradeView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

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