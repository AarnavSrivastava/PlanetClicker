package com.example.planetclicker

import android.animation.Animator
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.CycleInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android.ReverseInterpolator
import kotlin.math.floor
import kotlin.random.Random


class UpgradeItemAdapter(private val dataSet: ArrayList<UpgradeItem>, mContext: Context) :
    RecyclerView.Adapter<UpgradeItemAdapter.ViewHolder>() {

    private var context: Context

    init {
        this.context = mContext
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val amount: TextView
        val owned: TextView
        val button: Button
        val image: ImageView
        val constLayout: ConstraintLayout

        init {
            // Define click listener for the ViewHolder's View
            title = view.findViewById(R.id.upgradeTitle)
            amount = view.findViewById(R.id.upgradeAmount)
            owned = view.findViewById(R.id.upgradeOwned)
            button = view.findViewById(R.id.upgradeButton)
            image = view.findViewById(R.id.imageView)
            constLayout = view.findViewById(R.id.constLayout2)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.upgrade_item, viewGroup, false)


        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.title.text = dataSet[position].name
        viewHolder.amount.text = "${dataSet[position].cost} metal"
        viewHolder.owned.text = "Owned: ${dataSet[position].count}"
        viewHolder.image.setImageResource(dataSet[position].image)

        viewHolder.button.text = if (dataSet[position].count.get() == 0) "Purchase" else "Upgrade"

        val customFont = ResourcesCompat.getFont(context, R.font.alegreya_sans_sc_bold)

        viewHolder.title.typeface = customFont
        viewHolder.amount.typeface = customFont
        viewHolder.owned.typeface = customFont
        viewHolder.button.typeface = customFont

        viewHolder.button.setOnClickListener {
            if (MainActivity.metal.get() >= dataSet[position].cost.get())
            {
                MainActivity.metal.set(MainActivity.metal.get() - dataSet[position].cost.get())
                MainActivity.disp.text = "Metal: ${floor(MainActivity.metal.get()).toInt()}"

                var mps: Double = MainActivity.mps.get()
                mps -= dataSet[position].income.get()/(1.0*dataSet[position].cooldown.get())

                dataSet[position].buyItem()

                mps += dataSet[position].income.get()/(1.0*dataSet[position].cooldown.get())

                mps = String.format("%.2f", mps).toDouble()

                MainActivity.mps.set(mps)
                MainActivity.mpsView.text = "Metal/Second: ${MainActivity.mps.get()}"
                this.notifyDataSetChanged()
            }
        }

        viewHolder.button.isEnabled = dataSet[position].enabled.get()

        if (viewHolder.button.isEnabled) {
            animate(viewHolder.button)
        }

        for (i in 1..dataSet[position].count.get()) {
            var image = ImageView(MainActivity.context)
            image.id = ConstraintLayout.generateViewId()
            image.setImageResource(dataSet[position].image)
            image.alpha = 0f

            val lp = ConstraintLayout.LayoutParams(200, 200)
            image.layoutParams = lp

            viewHolder.constLayout.addView(image)

            val set = ConstraintSet()
            set.clone(viewHolder.constLayout)

            set.connect(image.id, ConstraintSet.TOP, viewHolder.constLayout.id, ConstraintSet.TOP, 30)
            set.connect(image.id, ConstraintSet.LEFT, viewHolder.constLayout.id, ConstraintSet.LEFT, 8000)

            image.animate()
                .setDuration(300)
                .alpha(1f)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                }).start()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
    fun animate(view: Button) {
        view.animate()
            .setDuration(6000000)
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setInterpolator(CycleInterpolator(6000000f/1000))
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    animate(view)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).start()
    }
}