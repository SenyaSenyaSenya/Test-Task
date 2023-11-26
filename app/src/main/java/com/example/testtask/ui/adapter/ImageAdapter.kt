package com.example.testtask.ui

import com.example.testtask.ui.DetailsScreenActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testtask.R
import com.example.testtask.data.ImageEntity
import android.content.Context
import android.content.Intent
import com.example.testtask.data.ImageDatabase

class ImageAdapter(private val context: Context, private val database: ImageDatabase) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private val images: MutableList<ImageEntity> = mutableListOf()

    fun setImages(images: List<ImageEntity>) {
        this.images.clear()
        this.images.addAll(images)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_form_bookmark, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        Glide.with(holder.itemView)
            .load(image.originalUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.imageView)
        holder.textViewName.text = image.photographer

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailsScreenActivity::class.java)
            intent.putExtra("fromBookmarkScreen", true)
            intent.putExtra("originalUrl", image.originalUrl)
            intent.putExtra("mediumUrl", image.mediumUrl)
            intent.putExtra("photographer", image.photographer)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewItem)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
    }
}