package com.mskwak.presentation.ui.fragment_diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.domain.model.Diary
import com.mskwak.presentation.databinding.LayoutItemDiaryBinding
import com.mskwak.presentation.ui.binding.setThumbnail
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DiaryListAdapter(
    private val onItemClick: (diary: Diary) -> Unit,
    private val getPlantName: (plantId: Int) -> String?
) : ListAdapter<Diary, DiaryListAdapter.ItemViewHolder>(ItemDiffCallback()) {

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

        fun bind(diary: Diary) {
            binding.tvMemo.text = diary.memo
            val pictureUri = diary.pictureList?.takeIf { it.isNotEmpty() }?.first()
            binding.ivPicture.setThumbnail(pictureUri)
            binding.tvPlantName.text = getPlantName(diary.plantId) ?: ""
            binding.tvDate.text = getDateText(diary.createdDate)
        }

        private fun getDateText(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("MM.dd\nE", Locale.getDefault())
            return date.format(formatter)
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