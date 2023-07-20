package com.mskwak.presentation.ui.dialog_diary.edit_diary

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.presentation.databinding.LayoutItemAddPictureBinding

class PictureListAdapter(private val onDeletePicture: (uri: Uri) -> Unit) :
    ListAdapter<Uri, PictureListAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemAddPictureBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holderItem: ItemViewHolder, position: Int) {
        holderItem.bind(getItem(position))
    }

    inner class ItemViewHolder(private val binding: LayoutItemAddPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivDelete.setOnClickListener {
                onDeletePicture.invoke(getItem(adapterPosition))
            }
        }

        fun bind(uri: Uri) {
            binding.pictureUri = uri
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }
    }
}