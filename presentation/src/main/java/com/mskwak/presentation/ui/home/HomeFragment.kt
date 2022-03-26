package com.mskwak.presentation.ui.home

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.mskwak.domain.AppConstValue
import com.mskwak.domain.usecase.PlantListSortOrder
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.FragmentHomeBinding
import com.mskwak.presentation.ui.base.BaseFragment
import com.mskwak.presentation.ui.custom_component.ListItemDecoVertical
import com.mskwak.presentation.ui.custom_component.SortAdapter
import com.mskwak.presentation.ui.plant_dialog.edit_plant.PlantEditDialog
import com.mskwak.presentation.ui.plant_dialog.plant_detail.PlantDetailDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutRes: Int = R.layout.fragment_home
    private val viewModel by viewModels<HomeViewModel>()
    private val adapter: PlantListAdapter by lazy { PlantListAdapter(viewModel) }
    private val swipeHelperCallback = SwipeHelperCallback().apply { setClamp(200f) }

    override fun initialize() {
        binding.viewModel = viewModel
        binding.fragment = this

        initRecyclerView()
        initSortSpinner()
        loadAd()

        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            adapter.submitList(plants)
        }
        viewModel.openPlantEvent.observe(viewLifecycleOwner) {
            openPlantDetail(it)
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
//        val swipeHelperCallback = SwipeHelperCallback().apply { setClamp(200f) }
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
        PlantDetailDialog(plantId).show(childFragmentManager, null)
    }

    fun onAddPlantClick() {
        PlantEditDialog(null).show(childFragmentManager, null)
    }

    private fun loadAd() {
        val adLoader = AdLoader.Builder(requireContext(), AppConstValue.AD_ID)
            .forNativeAd { nativeAd ->
                //show the ad
                binding.noInternet.visibility = View.GONE
                val template = binding.adTemplateView
                template.setNativeAd(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    binding.noInternet.visibility = View.VISIBLE
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
}