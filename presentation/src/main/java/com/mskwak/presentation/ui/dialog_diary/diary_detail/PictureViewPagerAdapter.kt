package com.mskwak.presentation.ui.dialog_diary.diary_detail

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.presentation.databinding.LayoutItemPicturePageBinding

class PictureViewPagerAdapter : RecyclerView.Adapter<PictureViewPagerAdapter.ItemViewHolder>() {
    var itemList: List<Uri>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemPicturePageBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        itemList?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = itemList?.size ?: 0

    class ItemViewHolder(private val binding: LayoutItemPicturePageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pictureUri: Uri) {
            binding.pictureUri = pictureUri
        }
    }
}