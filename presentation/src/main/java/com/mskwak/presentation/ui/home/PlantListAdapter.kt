package com.mskwak.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.domain.model.Plant
import com.mskwak.domain.model.WateringDays
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.LayoutItemPlantBinding
import java.time.LocalDate

class PlantListAdapter(
    private val onWateringClick: (plant: Plant) -> Unit,
    private val onItemClick: (plant: Plant) -> Unit,
    private val getWateringDays: (plant: Plant) -> WateringDays
) : ListAdapter<Plant, PlantListAdapter.ItemViewHolder>(ItemDiffCallback()) {

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
            binding.tvWatering.setOnClickListener {
                onWateringClick.invoke(getItem(adapterPosition))
            }
            binding.layoutContainer.setOnClickListener {
                onItemClick.invoke(getItem(adapterPosition))
            }
        }

        fun bind(plant: Plant) {
            binding.plant = plant
            setWateringDays(plant)
        }

        private fun setWateringDays(plant: Plant) {
            val wateringDays = getWateringDays(plant)
            val days = wateringDays.days
            val isDateOver = wateringDays.isDateOver
            val textFormatResId = if (isDateOver) {
                R.string.watering_d_day_plus_format
            } else {
                R.string.watering_d_day_minus_format
            }

            binding.tvWateringDays.text = itemView.context.getString(textFormatResId, days)

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

            binding.ivWateringIcon.setImageResource(waterIconRes)
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem == newItem

        }
    }
}