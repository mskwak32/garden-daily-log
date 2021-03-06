package com.mskwak.presentation.ui.home

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.mskwak.domain.usecase.PlantListSortOrder
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.FragmentHomeBinding
import com.mskwak.presentation.ui.base.BaseFragment
import com.mskwak.presentation.ui.custom_component.ListItemDecoVertical
import com.mskwak.presentation.ui.custom_component.SortAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutRes: Int = R.layout.fragment_home
    private val viewModel by viewModels<HomeViewModel>()
    private val adapter: PlantListAdapter by lazy {
        PlantListAdapter(
            onWateringClick = {
                viewModel.onWateringClick(it)
            }, onItemClick = {
                openPlantDetail(it.id)
            }, getDdays = {
                viewModel.getDdays(it)
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
            swipeHelperCallback.removeCurrentClamp(binding.plantListView)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {
        binding.plantListView.adapter = adapter
        val dividerHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics)
                .toInt()

        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.plantListView)
        binding.plantListView.apply {
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
        binding.sortSpinner.apply {
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
        val action = HomeFragmentDirections.homeToPlantDetail(plantId)
        findNavController().navigate(action)
    }

    fun onAddPlantClick() {
        val action = HomeFragmentDirections.homeToPlantEdit(null)
        findNavController().navigate(action)
    }
}