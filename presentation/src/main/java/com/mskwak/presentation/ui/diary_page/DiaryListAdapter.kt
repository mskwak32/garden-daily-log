package com.mskwak.presentation.ui.diary_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.presentation.databinding.LayoutItemDiaryBinding
import com.mskwak.presentation.model.DiaryUiData
import com.mskwak.presentation.ui.binding.setUri
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DiaryListAdapter(
    private val onItemClick: (diary: DiaryUiData) -> Unit,
    private val getPlantName: (plantId: Int) -> String?
) : ListAdapter<DiaryUiData, DiaryListAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            LayoutItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(private val binding: LayoutItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick.invoke(getItem(adapterPosition))
            }
        }

        fun bind(diary: DiaryUiData) {
            binding.memoText.text = diary.memo
            if (diary.pictureList?.isNotEmpty() == true) {
                binding.picture.setUri(diary.pictureList.first(), true)
            }
            binding.plantName.text = getPlantName(diary.plantId) ?: ""

            binding.dateText.text = if (isHeader(adapterPosition)) {
                getDateText(diary.createdDate)
            } else " "
        }

        private fun isHeader(position: Int): Boolean {
            if (position == 0) return true

            val previousDate = getItem(position).createdDate.dayOfMonth
            val currentDate = getItem(position).createdDate.dayOfMonth
            return previousDate != currentDate
        }

        private fun getDateText(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("MM.dd\nE", Locale.getDefault())
            return date.format(formatter)
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