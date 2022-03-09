package com.mskwak.presentation.plant.plant_detail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.domain.model.Record
import com.mskwak.presentation.binding.setUri
import com.mskwak.presentation.databinding.LayoutItemDiaryBinding
import com.mskwak.presentation.model.RecordImpl

class DiaryListAdapter(private val viewModel: PlantDetailViewModel) :
    ListAdapter<Record, DiaryListAdapter.ItemViewHolder>(ItemDiffCallback()) {

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

        fun bind(record: Record) {
            binding.record = record
            setPicture(record)
        }

        private fun setPicture(record: Record) {
            if (record.pictureList?.isNotEmpty() == true) {
                binding.picture.setUri(record.pictureList!!.first(), true)
            }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return if (oldItem is RecordImpl && newItem is RecordImpl) {
                oldItem == newItem
            } else false
        }
    }
}