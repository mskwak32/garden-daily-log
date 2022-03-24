package com.mskwak.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.domain.model.Plant
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.LayoutItemPlantBinding
import com.mskwak.presentation.model.PlantImpl
import java.time.LocalDate

class PlantListAdapter(private val viewModel: HomeViewModel) :
    ListAdapter<Plant, PlantListAdapter.ItemViewHolder>(ItemDiffCallback()) {

    private val currentDate = LocalDate.now()

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

    inner class ItemViewHolder(private val binding: LayoutItemPlantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            binding.plant = plant
            setDday(plant)
        }

        private fun setDday(plant: Plant) {
            val pair = binding.viewModel?.getDdays(plant)
            val dateText = pair?.first ?: ""
            val isDateOver = pair?.second ?: false

            binding.dDayCount.text = dateText

            val waterIconRes = when {
                isDateOver -> {
                    R.drawable.ic_water_drop_red
                }
                plant.lastWateringDate == currentDate -> {
                    R.drawable.ic_water_drop_with_background
                }
                else -> {
                    R.drawable.ic_water_drop_blue
                }
            }

            binding.waterIcon.setImageResource(waterIconRes)
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