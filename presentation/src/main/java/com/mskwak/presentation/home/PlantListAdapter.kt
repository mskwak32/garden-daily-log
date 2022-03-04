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
    ListAdapter<Plant, PlantListAdapter.ViewHolder>(PlantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemPlantBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    inner class ViewHolder(private val binding: LayoutItemPlantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = viewModel
        }

        fun bind(plant: Plant) {
            binding.apply {
                this.plant = plant
            }
            setDday(viewModel, plant)
        }

        private fun setDday(viewModel: HomeViewModel, plant: Plant) {
            viewModel.getRemainWateringDate(plant) { days ->
                binding.dDayCount.text = String.format("D-%02d", days)
            }
        }
    }
}

class PlantDiffCallback : DiffUtil.ItemCallback<Plant>() {
    override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
        return if (oldItem is PlantImpl && newItem is PlantImpl) {
            oldItem == newItem
        } else false
    }
}