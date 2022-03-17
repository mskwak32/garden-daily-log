package com.mskwak.presentation.diary_page

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.LayoutChipBinding

class FilterAdapter(private val viewModel: DiaryViewModel) :
    RecyclerView.Adapter<FilterAdapter.ItemViewHolder>() {
    private var plantIdList = listOf<Int>()
    private var plantNameMap = mapOf<Int, String>()

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = LayoutChipBinding.inflate(LayoutInflater.from(parent.context))
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(plantIdList[position])
    }

    override fun getItemCount(): Int = plantIdList.size

    fun setItemMap(plantNameMap: Map<Int, String>) {
        plantIdList = listOf(DiaryViewModel.SELECT_ALL_KEY) + plantNameMap.keys.toList()
        this.plantNameMap = plantNameMap
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(private val binding: LayoutChipBinding) :
        RecyclerView.ViewHolder(binding.chip) {

        init {
            itemView.setOnClickListener {
                viewModel.setPlantFilter(plantIdList[adapterPosition])
                notifyItemChanged(selectedPosition)
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(plantId: Int) {
            binding.chip.text = if (plantId == DiaryViewModel.SELECT_ALL_KEY) {
                itemView.context.getString(R.string.select_all)
            } else {
                plantNameMap[plantId]
            }
            binding.chip.isChecked = if (plantId == viewModel.selectedPlantId) {
                selectedPosition = adapterPosition
                true
            } else false
        }
    }
}