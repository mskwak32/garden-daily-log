package com.mskwak.presentation.ui.dialog_plant.plant_detail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.domain.model.Diary
import com.mskwak.presentation.databinding.LayoutItemDiaryInPlantDetailBinding
import com.mskwak.presentation.ui.binding.setThumbnail

class DiarySummaryAdapter(private val onItemClick: (diary: Diary) -> Unit) :
    ListAdapter<Diary, DiarySummaryAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemDiaryInPlantDetailBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: LayoutItemDiaryInPlantDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick.invoke(getItem(adapterPosition))
            }
        }

        fun bind(diary: Diary) {
            binding.diary = diary
            setPicture(diary)
        }

        private fun setPicture(diary: Diary) {
            val pictureUri = diary.pictureList?.takeIf { it.isNotEmpty() }?.first()
            binding.ivPicture.setThumbnail(pictureUri)
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Diary>() {
        override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem == newItem
        }
    }
}