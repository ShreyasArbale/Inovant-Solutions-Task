package com.example.testapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.R

class CircleIndicatorAdapter(private val images: List<String>, private var selectedPosition: Int) :
    RecyclerView.Adapter<CircleIndicatorAdapter.CircleIndicatorViewHolder>() {

    inner class CircleIndicatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circleView: ImageView = itemView.findViewById(R.id.circleIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleIndicatorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.circle_indicator_item, parent, false)
        return CircleIndicatorViewHolder(view)
    }

    override fun onBindViewHolder(holder: CircleIndicatorViewHolder, position: Int) {
        if (position == selectedPosition) {
            holder.circleView.setImageResource(R.drawable.selected_circle)
        } else {
            holder.circleView.setImageResource(R.drawable.unselected_circle)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun updateSelectedPosition(newPosition: Int) {
        val oldPosition = selectedPosition
        selectedPosition = newPosition
        notifyItemChanged(oldPosition)
        notifyItemChanged(newPosition)
    }
}
