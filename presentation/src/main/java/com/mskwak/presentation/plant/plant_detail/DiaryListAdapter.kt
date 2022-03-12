package com.mskwak.presentation.plant.plant_detail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.domain.model.Diary
import com.mskwak.presentation.R
import com.mskwak.presentation.binding.setUri
import com.mskwak.presentation.databinding.LayoutItemDiaryBinding
import com.mskwak.presentation.model.DiaryImpl

class DiaryListAdapter(private val viewModel: PlantDetailViewModel) :
    ListAdapter<Diary, DiaryListAdapter.ItemViewHolder>(ItemDiffCallback()) {

    var onItemClickListener: ((diary: Diary) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemDiaryBinding.inflate(inflater, parent, false).apply {
            viewModel = this@DiaryListAdapter.viewModel
        }
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
                onItemClickListener?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(diary: Diary) {
            binding.diary = diary
            setPicture(diary)
        }

        private fun setPicture(diary: Diary) {
            if (diary.pictureList?.isNotEmpty() == true) {
                binding.picture.setUri(diary.pictureList!!.first(), true)
            } else {
                binding.picture.setBackgroundResource(R.drawable.plant_default)
            }
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