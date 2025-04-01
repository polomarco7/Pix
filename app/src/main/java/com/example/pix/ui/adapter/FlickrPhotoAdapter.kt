package com.example.pix.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pix.data.flickr.dto.PhotoDto
import com.example.pix.databinding.ItemImageBinding

class FlickrPhotoAdapter(
    private val onItemClick: (PhotoDto) -> Unit
) : PagingDataAdapter<PhotoDto, FlickrPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    inner class PhotoViewHolder(
        private val binding: ItemImageBinding,
        private val onClick: (PhotoDto) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: PhotoDto) {
            binding.imageView.layoutParams.height = binding.root.width
            Glide.with(binding.root)
                .load(photo.getImageUrl("q"))
                .centerCrop()
                .into(binding.imageView)
            binding.textView.text = photo.title
            binding.root.setOnClickListener { onClick(photo) }
        }
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<PhotoDto>() {
            override fun areItemsTheSame(oldItem: PhotoDto, newItem: PhotoDto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PhotoDto, newItem: PhotoDto): Boolean {
                return oldItem == newItem
            }
        }
        const val PHOTO_ITEM_TYPE = 0
        const val LOADING_ITEM_TYPE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhotoViewHolder(
        ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onItemClick
    )

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) PHOTO_ITEM_TYPE
        else LOADING_ITEM_TYPE
    }
}