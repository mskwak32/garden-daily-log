package com.mskwak.presentation.ui.diary_page

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.domain.model.Diary
import com.mskwak.presentation.databinding.LayoutItemDiaryBinding
import com.mskwak.presentation.ui.binding.setUri
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DiaryListAdapter(private val viewModel: DiaryViewModel) :
    RecyclerView.Adapter<DiaryListAdapter.ItemViewHolder>() {
    private var itemList: List<Diary> = emptyList()

    var onClickListener: ((diary: Diary) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            LayoutItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Diary>) {
        itemList = list
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: LayoutItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onClickListener?.invoke(itemList[adapterPosition])
            }
        }

        fun bind(diary: Diary) {
            binding.memoText.text = diary.memo
            if (diary.pictureList?.isNotEmpty() == true) {
                binding.picture.setUri(diary.pictureList?.first(), true)
            }
            binding.plantName.text = viewModel.plantNameMap.value?.get(diary.plantId) ?: ""

            binding.dateText.text = if (isHeader(adapterPosition)) {
                getDateText(diary.createdDate)
            } else " "
        }

        private fun isHeader(position: Int): Boolean {
            if (position == 0) return true

            val previousDate = itemList[position - 1].createdDate.dayOfMonth
            val currentDate = itemList[position].createdDate.dayOfMonth
            return previousDate != currentDate
        }

        private fun getDateText(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("MM.dd\nE", Locale.getDefault())
            return date.format(formatter)
        }
    }
}