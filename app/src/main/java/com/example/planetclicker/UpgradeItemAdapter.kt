package com.example.planetclicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UpgradeItemAdapter(private val dataSet: ArrayList<UpgradeItem>) :
    RecyclerView.Adapter<UpgradeItemAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val amount: TextView
        val button: Button
        val image: ImageView

        init {
            // Define click listener for the ViewHolder's View
            title = view.findViewById(R.id.upgradeTitle)
            amount = view.findViewById(R.id.upgradeAmount)
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
        viewHolder.image.setImageResource(dataSet[position].image)


    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
