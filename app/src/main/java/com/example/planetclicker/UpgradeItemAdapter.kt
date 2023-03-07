package com.example.planetclicker

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.floor


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

        init {
            // Define click listener for the ViewHolder's View
            title = view.findViewById(R.id.upgradeTitle)
            amount = view.findViewById(R.id.upgradeAmount)
            owned = view.findViewById(R.id.upgradeOwned)
            button = view.findViewById(R.id.upgradeButton)
            image = view.findViewById(R.id.imageView)
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

        viewHolder.button.text = if (dataSet[position].count == 0) "Purchase" else "Upgrade"

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
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
