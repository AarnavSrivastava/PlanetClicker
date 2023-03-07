package com.example.planetclicker

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.Button
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.floor
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    companion object {
        @JvmStatic var metal: AtomicReference<Double> = AtomicReference<Double>(0.0)
        @JvmStatic lateinit var disp: MaterialTextView
        @JvmStatic lateinit var mpsView: TextView
        @JvmStatic var mps: AtomicReference<Double> = AtomicReference<Double>(0.0)
    }
    var currentPerClick: AtomicInteger = AtomicInteger(1)
    var multiplier: AtomicReference<Double> = AtomicReference<Double>(1.0)

    lateinit var mainActivity: ConstraintLayout
    lateinit var button: MaterialButton
    lateinit var rav: RiveAnimationView
    lateinit var touchRav: Button

    var items: ArrayList<UpgradeItem> = arrayListOf(UpgradeItem("Manual Labor", R.drawable.miner, AtomicInteger(30), AtomicInteger(5)))
    var upgradeItemAdapter = UpgradeItemAdapter(items, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        disp = findViewById(R.id.label)
        button = findViewById(R.id.button)
        mainActivity = findViewById(R.id.layout)
        touchRav = findViewById(R.id.touchRav)
        rav = findViewById(R.id.rav)
        mpsView = findViewById(R.id.perSec)

        touchRav.apply {
            setOnTouchListener(@SuppressLint("ClickableViewAccessibility")
            object : View.OnTouchListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(view: View, event: MotionEvent): Boolean {

                    return when (MotionEventCompat.getActionMasked(event)) {
                        MotionEvent.ACTION_DOWN -> {
                            rav.setBooleanState("State Machine 1", "Pressed", true)

                            metal.set(metal.get()+currentPerClick.get() * multiplier.get())

                            val x = event.x.toInt()
                            val y = event.y.toInt()

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
                            set.connect(metalImage.id, ConstraintSet.TOP, mainActivity.id, ConstraintSet.TOP, y+700)
                            set.connect(metalImage.id, ConstraintSet.LEFT, mainActivity.id, ConstraintSet.LEFT, x+250)

                            set.connect(incrementText.id, ConstraintSet.TOP, mainActivity.id, ConstraintSet.TOP, y+720)
                            set.connect(incrementText.id, ConstraintSet.LEFT, mainActivity.id, ConstraintSet.LEFT, x+260+Random.nextInt(-30,30))
                            set.applyTo(mainActivity);

                            metalImage.animate()
                                .setDuration(600)
                                .alpha(0f)
                                .rotation(Random.nextInt(-90, 90)+0f)
                                .translationY(300f)
                                .translationX(Random.nextInt(-300,300)+0f)
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

//                                Log.d("EXCEPTION", e.toString())


                            disp.text = "Metal: ${floor(metal.get()).toInt()}"

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

        GlobalScope.launch {
            while (true) {
                sleep(1000)

                Log.d("COUNT", "${metal.get()}")

                for (item in items) {
                    if (item.count != 0) {
                        metal.set(metal.get() + item.income.get() * multiplier.get()/item.cooldown.get())
                        disp.text = "Metal: ${floor(metal.get()).toInt()}"
                    }
                }
            }
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