package com.mskwak.presentation.diary.edit_diary

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.presentation.databinding.LayoutItemPictureBinding

class PictureListAdapter(private val viewModel: EditDiaryViewModel) :
    ListAdapter<Uri, PictureListAdapter.ItemViewHolder>(PictureDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemPictureBinding.inflate(inflater, parent, false).apply {
            this.viewModel = this@PictureListAdapter.viewModel
        }
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holderItem: ItemViewHolder, position: Int) {
        holderItem.bind(getItem(position))
    }

    inner class ItemViewHolder(private val binding: LayoutItemPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {
            binding.pictureUri = uri
        }
    }

    class PictureDiffCallback : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }
    }
}