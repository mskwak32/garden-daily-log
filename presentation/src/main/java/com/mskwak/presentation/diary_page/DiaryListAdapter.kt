package com.mskwak.presentation.diary_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.domain.model.Diary
import com.mskwak.presentation.binding.setUri
import com.mskwak.presentation.databinding.LayoutItemDiaryBinding
import com.mskwak.presentation.model.DiaryImpl
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DiaryListAdapter(private val viewModel: DiaryViewModel) :
    ListAdapter<Diary, DiaryListAdapter.ItemViewHolder>(ItemDiffCallback()) {

    var onClickListener: ((diary: Diary) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            LayoutItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    inner class ItemViewHolder(private val binding: LayoutItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onClickListener?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(diary: Diary) {
            binding.memoText.text = diary.memo
            if (diary.pictureList?.isNotEmpty() == true) {
                binding.picture.setUri(diary.pictureList?.first(), true)
            }
            binding.plantName.text = viewModel.plantNameMap.value?.get(diary.plantId) ?: ""

            if (isHeader(adapterPosition)) {
                binding.dateText.text = getDateText(diary.createdDate)
            }
        }

        private fun isHeader(position: Int): Boolean {
            if (position == 0) return true

            val previousDate = getItem(position - 1).createdDate.dayOfMonth
            val currentDate = getItem(position).createdDate.dayOfMonth
            return previousDate != currentDate
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
            return if (oldItem is DiaryImpl && newItem is DiaryImpl) {
                oldItem == newItem
            } else false
        }
    }
}