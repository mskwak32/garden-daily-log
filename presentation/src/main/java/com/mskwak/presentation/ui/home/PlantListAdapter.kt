package com.mskwak.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.LayoutItemPlantBinding
import com.mskwak.presentation.model.PlantUiData
import java.time.LocalDate

class PlantListAdapter(
    private val onWateringClick: (plant: PlantUiData) -> Unit,
    private val onItemClick: (plant: PlantUiData) -> Unit,
    private val getDdays: (plant: PlantUiData) -> Pair<String, Boolean>
) :
    ListAdapter<PlantUiData, PlantListAdapter.ItemViewHolder>(ItemDiffCallback()) {

    private val currentDate = LocalDate.now()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemPlantBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holderItem: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holderItem.bind(item)
    }

    inner class ItemViewHolder(private val binding: LayoutItemPlantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.wateringButton.setOnClickListener {
                onWateringClick.invoke(getItem(adapterPosition))
            }
            binding.container.setOnClickListener {
                onItemClick.invoke(getItem(adapterPosition))
            }
        }

        fun bind(plant: PlantUiData) {
            binding.plant = plant
            setDday(plant)
        }

        private fun setDday(plant: PlantUiData) {
            val pair = getDdays(plant)
            val dateText = pair.first
            val isDateOver = pair.second

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

    class ItemDiffCallback : DiffUtil.ItemCallback<PlantUiData>() {
        override fun areItemsTheSame(oldItem: PlantUiData, newItem: PlantUiData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlantUiData, newItem: PlantUiData): Boolean {
            return oldItem == newItem

        }
    }
}