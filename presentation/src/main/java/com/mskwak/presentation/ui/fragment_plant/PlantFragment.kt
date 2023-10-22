package com.mskwak.presentation.ui.fragment_plant

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.mskwak.domain.type.PlantListSortOrder
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.FragmentPlantBinding
import com.mskwak.presentation.ui.base.BaseFragment
import com.mskwak.presentation.ui.custom_component.ListItemDecoVertical
import com.mskwak.presentation.ui.custom_component.SortAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlantFragment : BaseFragment<FragmentPlantBinding>() {
    override val layoutRes: Int = R.layout.fragment_plant
    private val viewModel by viewModels<PlantViewModel>()
    private val adapter: PlantListAdapter by lazy {
        PlantListAdapter(
            onWateringClick = {
                viewModel.onWateringClick(it)
            }, onItemClick = {
                openPlantDetail(it.id)
            }, getWateringDays = {
                viewModel.getWateringDays(it)
            }
        )
    }
    private val swipeHelperCallback = SwipeHelperCallback().apply { setClamp(200f) }

    override fun initialize() {
        binding.viewModel = viewModel
        binding.fragment = this

        initRecyclerView()
        initSortSpinner()

        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            adapter.submitList(plants)
        }
        viewModel.onWateringEvent.observe(viewLifecycleOwner) {
            swipeHelperCallback.removeCurrentClamp(binding.rvPlantList)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {
        binding.rvPlantList.adapter = adapter
        val dividerHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics)
                .toInt()

        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvPlantList)
        binding.rvPlantList.apply {
            addItemDecoration(ListItemDecoVertical(dividerHeight))
            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }
    }

    private fun initSortSpinner() {
        val spinnerAdapter = SortAdapter(
            requireContext(),
            R.layout.layout_spinner_item,
            resources.getStringArray(R.array.home_sort_array)
        )
        binding.spinnerSort.apply {
            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    spinnerAdapter.selectedItemPosition = p2
                    viewModel.setSortOrder(PlantListSortOrder.values()[p2])
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //do nothing
                }
            }
        }
    }

    private fun openPlantDetail(plantId: Int) {
        val action = PlantFragmentDirections.toPlantDetail(plantId)
        findNavController().navigate(action)
    }

    fun onAddPlantClick() {
        val action = PlantFragmentDirections.toPlantEdit(null)
        findNavController().navigate(action)
    }
}