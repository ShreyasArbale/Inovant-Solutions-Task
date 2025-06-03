package com.example.testapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.testapp.R
import com.example.testapp.model.ConfigurableOption

class ColorAdapter(private val colorOption: List<ConfigurableOption>) :
    RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorView: ImageView = itemView.findViewById(R.id.colorView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.color_view_item, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val imageUrl = colorOption.first().attributes[position].images.firstOrNull()

        holder.colorView.load(imageUrl) {
            transformations(CircleCropTransformation())
        }
    }

    override fun getItemCount(): Int {
        return colorOption.first().attributes.size
    }
}
