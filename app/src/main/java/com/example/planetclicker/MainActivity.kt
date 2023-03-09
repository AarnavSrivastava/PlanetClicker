package com.example.planetclicker

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout.generateViewId
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.rive.runtime.kotlin.RiveAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

/*
* Just a few notes:
*  1. I used Kotlin (sorry Java)
*  2. For my animated "cookie" (planet) and alert icon for when you have enough metal to purchase an upgrade, I chose to use the Rive animation library (rive.app)
*  3. My upgrades are located within a RecyclerView within a BottomSheet
* */


class MainActivity : AppCompatActivity() {
    companion object {
        @JvmStatic var metal: AtomicReference<Double> = AtomicReference<Double>(0.0)
        @JvmStatic lateinit var disp: MaterialTextView
        @JvmStatic lateinit var mpsView: TextView
        @JvmStatic var mps: AtomicReference<Double> = AtomicReference<Double>(0.0)
        @JvmStatic lateinit var context: Context
    }
    var currentPerClick: AtomicInteger = AtomicInteger(1)
    var multiplier: AtomicReference<Double> = AtomicReference<Double>(1.0)

    lateinit var mainActivity: ConstraintLayout
    lateinit var button: ImageButton
    lateinit var rav: RiveAnimationView
    lateinit var alert: RiveAnimationView
    lateinit var touchRav: Button

    var items: ArrayList<UpgradeItem> = arrayListOf(UpgradeItem("Manual Labor", R.drawable.miner, AtomicInteger(30), AtomicInteger(5), AtomicInteger(1)), UpgradeItem("Mini Laser", R.drawable.satellite, AtomicInteger(100), AtomicInteger(10), AtomicInteger(25)))
    var upgradeItemAdapter = UpgradeItemAdapter(items, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        context = applicationContext

        disp = findViewById(R.id.label)
        button = findViewById(R.id.button)
        mainActivity = findViewById(R.id.layout)
        touchRav = findViewById(R.id.touchRav)
        rav = findViewById(R.id.rav)
        alert = findViewById(R.id.rav2)
        mpsView = findViewById(R.id.perSec)

        touchRav.apply {
            setOnTouchListener(@SuppressLint("ClickableViewAccessibility")
            object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(view: View, event: MotionEvent): Boolean {

                    return when (MotionEventCompat.getActionMasked(event)) {
                        MotionEvent.ACTION_DOWN -> {
                            rav.setBooleanState("State Machine 1", "Pressed", true)

                            metal.set(((metal.get()+currentPerClick.get() * multiplier.get())*1000.0).roundToInt()/1000.0)

                            val x = event.rawX.toInt()
                            val y = event.rawY.toInt()

                            val lp = LayoutParams(100,100)
                            val lp2 = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
                            val set = ConstraintSet()

                            var metalImage = ImageView(applicationContext)
                            metalImage.id = generateViewId()
                            metalImage.setImageResource(R.drawable.copper)

                            var incrementText = TextView(applicationContext)
                            incrementText.text = "+${(currentPerClick.get() * multiplier.get()).toInt()}"
                            incrementText.textSize = 18f
                            incrementText.setTextColor(resources.getColor(R.color.white))
                            incrementText.id = generateViewId()
                            val customFont = ResourcesCompat.getFont(applicationContext, R.font.aldrich)
                            incrementText.typeface = customFont

                            metalImage.layoutParams = lp
                            incrementText.layoutParams = lp2

                            mainActivity.addView(metalImage)
                            mainActivity.addView(incrementText)

                            set.clone(mainActivity)
                            set.connect(metalImage.id, ConstraintSet.TOP, mainActivity.id, ConstraintSet.TOP, y-300)
                            set.connect(metalImage.id, ConstraintSet.LEFT, mainActivity.id, ConstraintSet.LEFT, x-30)

                            set.connect(incrementText.id, ConstraintSet.TOP, mainActivity.id, ConstraintSet.TOP, y-300)
                            set.connect(incrementText.id, ConstraintSet.LEFT, mainActivity.id, ConstraintSet.LEFT, x+Random.nextInt(-30,30)-15)
                            set.applyTo(mainActivity);

                            metalImage.animate()
                                .setDuration(600)
                                .alpha(0f)
                                .rotation(Random.nextInt(-90, 90)+0f)
                                .translationY(400f)
                                .translationX(Random.nextInt(-400,400)+0f)
                                .setListener(object : Animator.AnimatorListener {
                                    override fun onAnimationStart(animation: Animator) {}
                                    override fun onAnimationEnd(animation: Animator) {
                                        (metalImage.parent as ViewGroup).removeView(metalImage)
                                    }
                                    override fun onAnimationCancel(animation: Animator) {}
                                    override fun onAnimationRepeat(animation: Animator) {}
                                }).start()

                            incrementText.animate()
                                .setDuration(1000)
                                .alpha(0f)
                                .translationY(-500f)
                                .translationX(Random.nextInt(-20,20)+0f)
                                .setListener(object : Animator.AnimatorListener {
                                    override fun onAnimationStart(animation: Animator) {}
                                    override fun onAnimationEnd(animation: Animator) {
                                        (incrementText.parent as ViewGroup).removeView(incrementText)
                                    }
                                    override fun onAnimationCancel(animation: Animator) {}
                                    override fun onAnimationRepeat(animation: Animator) {}
                                }).start()


                            disp.text = "Metal: ${floor(metal.get()).toInt()}"

                            var count = 0

                            for (item in items) {
                                if (item.cost.get() <= metal.get() && item.enabled.get()) {
                                    count++
                                }

                                if (item.cost.get() <= metal.get() && !item.enabled.get()) {
                                    runOnUiThread {
                                        item.enabled.set(true)
                                        upgradeItemAdapter.notifyDataSetChanged()
                                    }
                                }

                                if (item.cost.get() <= metal.get() && alert.visibility == GONE) {
                                    count++
                                    runOnUiThread {
                                        alert.visibility = VISIBLE
                                        alert.setBooleanState("State Machine 1", "Playing", true)
                                    }
                                }
                                else if (alert.visibility != GONE && count == 0) {
                                    runOnUiThread {
                                        alert.visibility = GONE
                                        alert.setBooleanState("State Machine 1", "Playing", false)
                                    }
                                }

                                if (item.cost.get() > metal.get() && item.enabled.get()) {
                                    runOnUiThread {
                                        item.enabled.set(false)
                                        upgradeItemAdapter.notifyDataSetChanged()
                                    }
                                }
                            }

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

        val passiveThread = Thread {
            while (true) {
                sleep(100)
                var count = 0

                for (item in items) {
                    if (item.count.get() != 0) {
                        metal.set(((metal.get() + item.income.get() * multiplier.get() /(10 * item.cooldown.get()))*1000.0).roundToInt()/1000.0)
                        disp.text = "Metal: ${floor(metal.get()).toInt()}"
                    }

                    if (item.cost.get() <= metal.get() && item.enabled.get()) {
                        count++
                    }

                    if (item.cost.get() <= metal.get() && !item.enabled.get()) {
                        runOnUiThread {
                            item.enabled.set(true)
                            upgradeItemAdapter.notifyDataSetChanged()
                        }
                    }

                    if (item.cost.get() <= metal.get() && alert.visibility == GONE) {
                        count++
                        runOnUiThread {
                            alert.visibility = VISIBLE
                            alert.setBooleanState("State Machine 1", "Playing", true)
                        }
                    }
                    else if (alert.visibility != GONE && count == 0) {
                        runOnUiThread {
                            alert.visibility = GONE
                            alert.setBooleanState("State Machine 1", "Playing", false)
                        }
                    }

                    if (item.cost.get() > metal.get() && item.enabled.get()) {
                        runOnUiThread {
                            item.enabled.set(false)
                            upgradeItemAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        passiveThread.start()


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