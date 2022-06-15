package com.mskwak.presentation.ui.plant_dialog.plant_detail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.presentation.databinding.LayoutItemDiaryInPlantDetailBinding
import com.mskwak.presentation.model.DiaryUiData
import com.mskwak.presentation.ui.binding.setThumbnail

class DiarySummaryAdapter(private val onItemClick: (diary: DiaryUiData) -> Unit) :
    ListAdapter<DiaryUiData, DiarySummaryAdapter.ItemViewHolder>(ItemDiffCallback()) {

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

        fun bind(diary: DiaryUiData) {
            binding.diary = diary
            setPicture(diary)
        }

        private fun setPicture(diary: DiaryUiData) {
            val pictureUri =
                if (diary.pictureList?.isNotEmpty() == true) diary.pictureList.first() else null
            binding.picture.setThumbnail(pictureUri)
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<DiaryUiData>() {
        override fun areItemsTheSame(oldItem: DiaryUiData, newItem: DiaryUiData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DiaryUiData, newItem: DiaryUiData): Boolean {
            return oldItem == newItem
        }
    }
}