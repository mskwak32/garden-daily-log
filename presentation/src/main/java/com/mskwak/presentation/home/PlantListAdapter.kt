package com.mskwak.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.domain.model.Plant
import com.mskwak.presentation.databinding.LayoutItemPlantBinding
import com.mskwak.presentation.model.PlantImpl

class PlantListAdapter(private val viewModel: HomeViewModel) :
    ListAdapter<Plant, PlantListAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemPlantBinding.inflate(inflater, parent, false).apply {
            this.viewModel = this@PlantListAdapter.viewModel
        }
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holderItem: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holderItem.bind(item)
    }


    class ItemViewHolder(private val binding: LayoutItemPlantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            binding.plant = plant
            setDday(plant)
        }

        private fun setDday(plant: Plant) {
            binding.dDayCount.text = binding.viewModel?.getDdays(plant) ?: ""
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return if (oldItem is PlantImpl && newItem is PlantImpl) {
                oldItem == newItem
            } else false
        }
    }
}