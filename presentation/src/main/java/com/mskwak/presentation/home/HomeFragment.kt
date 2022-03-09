package com.mskwak.presentation.home

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.mskwak.domain.model.Plant
import com.mskwak.domain.usecase.PlantListSortOrder
import com.mskwak.presentation.R
import com.mskwak.presentation.base.BaseFragment
import com.mskwak.presentation.custom_component.ListItemDecoVertical
import com.mskwak.presentation.databinding.FragmentHomeBinding
import com.mskwak.presentation.dialog.DeletePlantConfirmDialog
import com.mskwak.presentation.plant.edit_plant.EditPlantDialog
import com.mskwak.presentation.plant.plant_detail.PlantDetailDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutRes: Int = R.layout.fragment_home
    private val viewModel by viewModels<HomeViewModel>()
    private val adapter: PlantListAdapter by lazy { PlantListAdapter(viewModel) }

    override fun initialize() {
        binding?.viewModel = viewModel
        binding?.fragment = this

        initRecyclerView()
        initSortSpinner()
        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            adapter.submitList(plants)
        }
        viewModel.openPlantEvent.observe(viewLifecycleOwner) {
            openPlantDetail(it)
        }
        viewModel.deletePlantClickEvent.observe(viewLifecycleOwner) {
            openDeleteConfirm(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {
        binding?.plantListView?.adapter = adapter
        val dividerHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics)
                .toInt()
        val swipeHelperCallback = SwipeHelperCallback().apply { setClamp(200f) }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding?.plantListView)
        binding?.plantListView?.apply {
            addItemDecoration(ListItemDecoVertical(dividerHeight))
            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }
    }

    private fun initSortSpinner() {
        val spinnerAdapter = object : ArrayAdapter<Any>(
            requireContext(),
            R.layout.layout_spinner_item,
            resources.getStringArray(R.array.home_sort_array)
        ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return super.getDropDownView(position, convertView, parent).also { view ->
                    if (position == binding?.sortSpinner?.selectedItemPosition) {
                        (view as? TextView)?.setTextColor(
                            resources.getColor(
                                R.color.green_600,
                                null
                            )
                        )
                    }
                }
            }
        }
        binding?.sortSpinner?.apply {
            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    viewModel.setSortOrder(PlantListSortOrder.values()[p2])
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //do nothing
                }
            }
        }
    }

    private fun openPlantDetail(plantId: Int) {
        PlantDetailDialog(plantId).show(childFragmentManager, null)
    }

    fun onAddPlantClick() {
        EditPlantDialog(null).show(childFragmentManager, null)
    }

    private fun openDeleteConfirm(plant: Plant) {
        DeletePlantConfirmDialog().apply {
            deleteClickListener = {
                viewModel.deletePlant(plant)
            }
        }.show(childFragmentManager, null)
    }
}